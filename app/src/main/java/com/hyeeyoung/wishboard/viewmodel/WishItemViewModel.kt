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
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hyeeyoung.wishboard.model.wish.WishItemRegistrationInfo
import com.hyeeyoung.wishboard.remote.AWSS3Service
import com.hyeeyoung.wishboard.repository.common.GalleryPagingDataSource
import com.hyeeyoung.wishboard.repository.common.GalleryRepository
import com.hyeeyoung.wishboard.repository.wish.WishRepository
import com.hyeeyoung.wishboard.util.getTimestamp
import com.hyeeyoung.wishboard.util.prefs
import com.hyeeyoung.wishboard.util.safeLet
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
import javax.inject.Inject

@HiltViewModel
class WishItemViewModel @Inject constructor(
    private val application: Application,
    private val wishRepository: WishRepository,
    private val galleryRepository: GalleryRepository,
) : ViewModel() {
    private var itemName = MutableLiveData<String>()
    private var itemPrice = MutableLiveData<String>()
    private var itemImage = MutableLiveData<String>()
    private var itemMemo = MutableLiveData<String>()
    private var itemUrl = MutableLiveData<String>()
    private var isCompleteUpload = MutableLiveData<Boolean?>()

    private val galleryImageUris = MutableLiveData<PagingData<Uri>>()
    private var selectedGalleryImageUri = MutableLiveData<Uri?>()
    private var cameraImageFile: File? = null

    private val token = prefs?.getUserToken()

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
        /* TODO 가격 데이터에 천단위 구분자 ',' 있는 경우 문자열 처리 필요
            itemImage 없어도 업로드 되도록 수정, image 파싱 못하는 경우, image null로 저장(not null -> null 허용), 불러올 때 기본 아이콘 보여주기
            itemImage -> itemImageUrl로 구체적인 변수명으로 변경 및 notiInfo 추가 */
        safeLet(
            itemName.value?.trim(),
            itemImage.value,
            itemUrl.value
        ) { itemName, imageUrl, siteUrl ->
            withContext(Dispatchers.IO) {
                val bitmap =
                    getBitmapFromURL(imageUrl) ?: return@withContext // TODO 업로드 실패 예외 처리 필요
                val imageFile = saveBitmapToInternalStorage(bitmap) ?: return@withContext

                val isSuccessful = AWSS3Service().uploadFile(imageFile.name, imageFile)
                if (!isSuccessful) return@withContext
                val wishItemRegistrationInfo =
                    WishItemRegistrationInfo(
                        itemName,
                        imageFile.name,
                        itemPrice.value?.toIntOrNull() ?: 0,
                        siteUrl,
                        itemMemo.value?.trim()
                    ) // TODO WishItem 으로 변경
                val isComplete = wishRepository.uploadWishItem(token, wishItemRegistrationInfo)
                isCompleteUpload.postValue(isComplete)
            }
        }
    }

    suspend fun uploadWishItemByBasics() {
        if (token == null) return
        safeLet(itemName.value?.trim(), selectedGalleryImageUri.value) { name, imageUrl ->
            withContext(Dispatchers.IO) {
                val file =
                    cameraImageFile ?: copyImageToInternalStorage(imageUrl) ?: return@withContext

                AWSS3Service().uploadFile(file.name, file)
                val wishItemRegistrationInfo =
                    WishItemRegistrationInfo(
                        name,
                        file.name,
                        itemPrice.value?.toIntOrNull() ?: 0,
                        itemUrl.value,
                        itemMemo.value?.trim()
                    )
                val isComplete = wishRepository.uploadWishItem(token, wishItemRegistrationInfo)
                isCompleteUpload.postValue(isComplete)
            }
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
        cameraImageFile = File(file.absoluteFile, fileName)
        return cameraImageFile
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

        var fileStream: FileOutputStream? = null
        try {
            fileStream = FileOutputStream(file)
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fileStream)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            try {
                file.deleteOnExit()
                fileStream!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }

    /** 갤러리 이미지를 내부저장소에 복사 */
    private fun copyImageToInternalStorage(uri: Uri): File? {
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

    fun onItemNameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        itemName.value = s.toString()
    }

    fun onItemPriceTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        itemPrice.value = s.toString().trim()
    }

    fun onItemUrlTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        itemUrl.value = s.toString().trim()
    }

    fun onItemMemoTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        itemMemo.value = s.toString()
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
    fun getItemPrice(): LiveData<String> = itemPrice
    fun getItemUrl(): LiveData<String> = itemUrl
    fun getItemMemo(): LiveData<String> = itemMemo
    fun isCompleteUpload(): LiveData<Boolean?> = isCompleteUpload

    fun getGalleryImageUris(): LiveData<PagingData<Uri>?> = galleryImageUris
    fun getSelectedGalleryImageUri(): LiveData<Uri?> = selectedGalleryImageUri

    companion object {
        private const val TAG = "WishItemViewModel"
    }
}