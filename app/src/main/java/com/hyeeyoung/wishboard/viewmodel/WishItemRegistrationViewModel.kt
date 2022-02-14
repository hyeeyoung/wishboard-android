package com.hyeeyoung.wishboard.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.Transformations
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.model.folder.FolderListViewType
import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.remote.AWSS3Service
import com.hyeeyoung.wishboard.repository.common.GalleryPagingDataSource
import com.hyeeyoung.wishboard.repository.common.GalleryRepository
import com.hyeeyoung.wishboard.repository.folder.FolderRepository
import com.hyeeyoung.wishboard.repository.wish.WishRepository
import com.hyeeyoung.wishboard.util.getTimestamp
import com.hyeeyoung.wishboard.util.prefs
import com.hyeeyoung.wishboard.util.safeLet
import com.hyeeyoung.wishboard.view.folder.adapters.FolderListAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.MalformedURLException
import java.net.URL
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class WishItemRegistrationViewModel @Inject constructor(
    private val application: Application,
    private val wishRepository: WishRepository,
    private val galleryRepository: GalleryRepository,
    private val folderRepository: FolderRepository,
) : ViewModel() {
    private var wishItem: WishItem? = null
    private var itemId: Long? = null
    private var itemName = MutableLiveData<String>()
    private var itemPrice = MutableLiveData<String>()
    private var itemImage = MutableLiveData<String>()
    private var itemMemo = MutableLiveData<String>()
    private var itemUrl = MutableLiveData<String>()
    private var folderItem: FolderItem? = null

    private var isCompleteUpload = MutableLiveData<Boolean?>()

    private val galleryImageUris = MutableLiveData<PagingData<Uri>>()
    private var selectedGalleryImageUri = MutableLiveData<Uri?>()
    private var imageFile: File? = null

    private val folderListAdapter =
        FolderListAdapter(application, FolderListViewType.HORIZONTAL_VIEW_TYPE)

    private val token = prefs?.getUserToken()

    init {
        fetchFolderList()
    }

    /** 오픈그래프 메타태그 파싱을 통해 아이템 정보 가져오기 */
    suspend fun getWishItemInfo(url: String) {
        try {
            val doc = Jsoup.connect(url).get()
            val ogTags = doc.select("meta[property^=og:]")
            val priceTags = doc.select("meta[property^=product:]")

            if (ogTags.size <= 0) return
            for (idx in ogTags.indices) {
                val tag = ogTags[idx]
                val text = tag.attr("property")
                when (text) {
                    "og:title" -> itemName.postValue(tag.attr("content")) // 상품명 데이터 파싱
                    "og:image" -> itemImage.postValue(tag.attr("content")) // 상품 이미지 데이터 파싱
                }
            }

            // 가격 데이터 파싱
            if (priceTags.size > 0) {
                for (i in priceTags.indices) {
                    val priceTag = priceTags[i]
                    val text = priceTag.attr("property")
                    if (text.matches(Regex(".*[pP]rice.*"))) {
                        val price = priceTag.attr("content")
                        if (price.isDigitsOnly()) {
                            itemPrice.postValue(price)
                            break
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun uploadWishItemByLinkSharing() {
        if (token == null) return
        // TODO 가격 데이터에 천단위 구분자 ',' 있는 경우 문자열 처리 필요
        safeLet(itemName.value?.trim(), itemUrl.value) { name, siteUrl ->
            withContext(Dispatchers.IO) {
                itemImage.value?.let { imageUrl ->
                    val bitmap = getBitmapFromURL(imageUrl) ?: return@let
                    imageFile = saveBitmapToInternalStorage(bitmap) ?: return@let
                    val isSuccessful = AWSS3Service().uploadFile(imageFile!!.name, imageFile!!)
                    if (!isSuccessful) return@withContext // TODO 업로드 실패 예외 처리 필요
                }

                val item = WishItem(
                    name = name,
                    image = imageFile?.name,
                    price = itemPrice.value?.replace(",", "")?.toIntOrNull(),
                    url = siteUrl,
                    memo = itemMemo.value?.trim()
                )
                val isComplete = wishRepository.uploadWishItem(token, item)
                isCompleteUpload.postValue(isComplete)
            }
        }
    }

    suspend fun uploadWishItemByBasics() {
        if (token == null) return
        safeLet(itemName.value?.trim(), selectedGalleryImageUri.value) { name, imageUri ->
            withContext(Dispatchers.IO) {
                val file = imageFile ?: copyImageToInternalStorage(imageUri) ?: return@withContext
                val isSuccessful = AWSS3Service().uploadFile(file.name, file)
                if (!isSuccessful) return@withContext

                val item = WishItem(
                    name = name,
                    image = file.name,
                    price = itemPrice.value?.replace(",", "")?.toIntOrNull(),
                    url = itemUrl.value,
                    memo = itemMemo.value?.trim(),
                    folderId = folderItem?.id,
                    folderName = folderItem?.name // TODO (보류) 현재 코드 상으로는 folderId만 필요한 것으로 파악되나 추후 수동등록화면에서 폴더 추가기능 도입할 경우 필요함
                )
                val isComplete = wishRepository.uploadWishItem(token, item)
                isCompleteUpload.postValue(isComplete)
            }
        }
    }

    suspend fun updateWishItem() {
        if (itemId == null || token == null) return
        val itemName = itemName.value?.trim() ?: return // TODO 상품명 필수 입력 토스트 띄우기

        withContext(Dispatchers.IO) {
            val file = imageFile ?: copyImageToInternalStorage(selectedGalleryImageUri.value)
            file?.let {
                val isSuccessful = AWSS3Service().uploadFile(it.name, it)
                if (!isSuccessful) return@withContext
            }

            wishItem = WishItem( // TODO 수정된 folder, noti 데이터로 초기화
                id = itemId,
                createAt = wishItem?.createAt,
                name = itemName,
                image = file?.name ?: wishItem?.image,
                price = itemPrice.value?.replace(",", "")?.toIntOrNull(),
                url = itemUrl.value,
                memo = itemMemo.value?.trim(),
                folderId = folderItem?.id ?: wishItem?.folderId,
                folderName = folderItem?.name ?: wishItem?.folderName, // TODO (보류) 현재 코드 상으로는 folderId만 필요한 것으로 파악되나 추후 수동등록화면에서 폴더 추가기능 도입할 경우 필요함
                notiDate = wishItem?.notiDate,
                notiType = wishItem?.notiType
            )

            val isComplete = wishRepository.updateWishItem(token, itemId!!, wishItem!!)
            isCompleteUpload.postValue(isComplete)
        }
    }

    private fun fetchFolderList() {
        if (token == null) return
        viewModelScope.launch {
            folderListAdapter.setData(
                folderRepository.fetchFolderListSummary(token) ?: return@launch
            )
        }
    }

    fun fetchGalleryImageUris(contentResolver: ContentResolver) {
        viewModelScope.launch {
            Pager(PagingConfig(pageSize = 10)) {
                GalleryPagingDataSource(contentResolver, galleryRepository)
            }.flow.cachedIn(viewModelScope)
                .collect { images ->
                    galleryImageUris.postValue(images)
                }
        }
    }

    fun createCameraImageFile(): File? {
        val file = File(application.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "wishtem")
        if (!file.exists()) {
            file.mkdirs()
        }

        val fileName = makePhotoFileName()
        imageFile = File(file.absoluteFile, fileName)
        return imageFile
    }

    /** 이미지 파일명 생성하는 함수로 해당 함수 호출 전 반드시 token null 체크해야함 */
    private fun makePhotoFileName(): String {
        val timestamp = getTimestamp()
        return ("${token!!.substring(7)}_${timestamp}.jpg")
    }

    /** 이미지 url을 bitmap으로 변환 */
    private suspend fun getBitmapFromURL(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val stream = url.openStream()
            BitmapFactory.decodeStream(stream)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /** bitmap 이미지를 내부저장소에 file로 저장 */
    private suspend fun saveBitmapToInternalStorage(bitmapImage: Bitmap): File? {
        val fileName = makePhotoFileName()
        val file = File(application.cacheDir, fileName)

        try {
            val fileStream = FileOutputStream(file)
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fileStream)
            file.deleteOnExit()
            fileStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return file
    }

    /** 갤러리 이미지를 내부저장소에 복사 */
    private fun copyImageToInternalStorage(uri: Uri?): File? {
        if (uri == null) return null
        val fileName = makePhotoFileName()
        val file = File(application.cacheDir, fileName)

        try {
            val inputStream = application.contentResolver.openInputStream(uri) ?: return null
            val outputStream: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) {
                outputStream.write(buf, 0, len)
            }
            file.deleteOnExit()
            outputStream.close()
            inputStream.close()
        } catch (ignore: IOException) {
            return null
        }
        return file
    }

    /** 가격 데이터에 천단위 구분자를 적용 */
    private fun applyPriceFormat(price: String): String? {
        // 천단위 구분자 포함 11자리 이상인 경우, 입력 불가하도록 바로 직전에 입력한 값을 반환
        if (price.length > 11) return price.substring(0, price.length - 1)
        
        // 숫자가 아닌 문자는 모두 제거
        val numPrice = price.replace(("[\\D.]").toRegex(), "")
        if (numPrice == "") return null
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(numPrice.toInt())
    }

    fun setWishItem(wishItem: WishItem) {
        this.wishItem = wishItem
        setWishItemInfo()
    }

    /** UI에 보여질 데이터값 설정 */
    private fun setWishItemInfo() {
        wishItem?.let { item ->
            itemId = item.id
            itemName.value = item.name
            itemImage.value = item.image
            itemPrice.value = item.price.toString()
            itemMemo.value = item.memo
            itemUrl.value = item.url
        }
    }

    fun onItemNameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        itemName.value = s.toString()
    }

    fun onItemPriceTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        itemPrice.value = s.toString()
    }

    fun onItemUrlTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        itemUrl.value = s.toString().trim()
    }

    fun onItemMemoTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        itemMemo.value = s.toString()
    }

    fun setFolderItem(folder: FolderItem) {
        folderItem = folder
    }

    fun setItemUrl(url: String) {
        itemUrl.value = url
    }

    fun setSelectedGalleryImageUri(imageUri: Uri?) {
        selectedGalleryImageUri.value = imageUri
    }

    fun setCompletedUpload(isCompleted: Boolean?) {
        isCompleteUpload.value = isCompleted
    }

    /*
     갤러리 이미지 선택 화면 진입 -> 아이템 등록 화면 복귀 -> 갤러리 이미지 선택 화면 재진입 concurrentmodificationexception 발생
     해당 예외 발생을 방지하고자 갤러리 이미지 선택 화면 진입 시 기존 이미지 clear
     */
    fun clearGalleryImageUris() {
        galleryImageUris.value = null
    }

    fun getItemName(): LiveData<String> = itemName
    fun getItemImage(): LiveData<String> = itemImage
    fun getItemPrice(): LiveData<String> = Transformations.map(itemPrice) { price ->
        applyPriceFormat(price)
    }
    fun getItemUrl(): LiveData<String> = itemUrl
    fun getItemMemo(): LiveData<String> = itemMemo
    fun getFolderItem(): FolderItem? = folderItem
    fun isCompleteUpload(): LiveData<Boolean?> = isCompleteUpload

    fun getGalleryImageUris(): LiveData<PagingData<Uri>?> = galleryImageUris
    fun getSelectedGalleryImageUri(): LiveData<Uri?> = selectedGalleryImageUri
    fun getWishItem(): WishItem? = wishItem

    fun getFolderListAdapter(): FolderListAdapter = folderListAdapter

    companion object {
        private const val TAG = "WishItemRegistrationViewModel"
    }
}