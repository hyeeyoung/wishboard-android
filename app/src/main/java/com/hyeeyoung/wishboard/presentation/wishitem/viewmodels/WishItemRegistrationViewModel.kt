package com.hyeeyoung.wishboard.presentation.wishitem.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.util.Patterns
import android.webkit.URLUtil
import android.widget.NumberPicker
import androidx.lifecycle.*
import com.hyeeyoung.wishboard.WishBoardApp
import com.hyeeyoung.wishboard.data.model.folder.FolderItem
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.repositories.FolderRepository
import com.hyeeyoung.wishboard.domain.repositories.WishRepository
import com.hyeeyoung.wishboard.presentation.common.types.ProcessStatus
import com.hyeeyoung.wishboard.presentation.folder.FolderListAdapter
import com.hyeeyoung.wishboard.presentation.folder.types.FolderListViewType
import com.hyeeyoung.wishboard.presentation.noti.types.NotiType
import com.hyeeyoung.wishboard.util.*
import com.hyeeyoung.wishboard.util.extension.toPlainNullableRequestBody
import com.hyeeyoung.wishboard.util.extension.toPlainRequestBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class WishItemRegistrationViewModel @Inject constructor(
    private val application: Application,
    private val wishRepository: WishRepository,
    private val folderRepository: FolderRepository,
) : ViewModel() {
    private val token = WishBoardApp.prefs.getUserToken()
    private var wishItem: WishItem? = null
    private var itemId: Long? = null
    private var itemName = MutableLiveData<String?>()
    private var itemPrice = MutableLiveData<String?>()
    private var itemImage = MutableLiveData<String?>()
    private var itemImageUrl = MutableLiveData<String?>()
    private var itemMemo = MutableLiveData<String?>()
    private var itemUrl = MutableLiveData<String?>()

    /** 유효하지 않은 url인 경우 또는 사이트 url 수정을 시도할 경우 원본 url을 보존하기위 위한 변수 */
    private var itemUrlInput = MutableLiveData<String?>()
    private var folderItem = MutableLiveData<FolderItem>()
    private var folderName = MutableLiveData<String?>()
    private var notiType = MutableLiveData<NotiType?>()
    private var notiDate = MutableLiveData<String?>()

    private var notiTypeVal = MutableLiveData<Int>()
    private var notiDateVal = MutableLiveData<Int>()
    private var notiHourVal = MutableLiveData<Int>()
    private var notiMinuteVal = MutableLiveData<Int>()

    private var isEnabledSaveButton = MediatorLiveData<Boolean>()
    private var isCompleteUpload = MutableLiveData<Boolean?>()
    private var isCompleteFolderUpload = MutableLiveData<Boolean?>()
    private var isExistFolderName = MutableLiveData<Boolean?>()
    private var isValidItemUrl = MutableLiveData<Boolean?>()

    private var _isShownItemNonUpdateDialog = MutableLiveData<Boolean?>()
    val isShownItemNonUpdateDialog: LiveData<Boolean?> get() = _isShownItemNonUpdateDialog

    private var itemRegistrationStatus = MutableLiveData<ProcessStatus>()
    private var folderRegistrationStatus = MutableLiveData<ProcessStatus>()

    private var selectedGalleryImageUri = MutableLiveData<Uri?>()
    private var imageFile: File? = null

    private val folderListSquareAdapter =
        FolderListAdapter(FolderListViewType.SQUARE_VIEW_TYPE)

    init {
        initEnabledSaveButton()
        fetchFolderList()
    }

    /** 오픈그래프 메타태그 파싱을 통해 아이템 정보 가져오기 */
    suspend fun getWishItemInfo(url: String) {
        val result = wishRepository.getItemParsingInfo(url) ?: return
        itemName.postValue(result.name)
        itemPrice.postValue(result.price.toString())
        itemImage.postValue(result.image)
    }

    suspend fun uploadWishItemByLinkSharing() {
        if (token == null) return
        if (itemRegistrationStatus.value == ProcessStatus.IN_PROGRESS) return
        itemRegistrationStatus.postValue(ProcessStatus.IN_PROGRESS)

        safeLet(itemName.value?.trim(), getValidUrl(itemUrl.value)) { name, siteUrl ->
            val folderId: RequestBody? =
                folderItem.value?.id?.toString()?.toPlainNullableRequestBody()
            val itemName: RequestBody = name.toPlainRequestBody()
            val itemPrice: RequestBody? =
                itemPrice.value?.replace(",", "")?.toIntOrNull()?.toString()
                    ?.toPlainNullableRequestBody() // // TODO 가격 데이터에 천단위 구분자 ',' 있는 경우 문자열 처리 필요
            val itemUrl: RequestBody = siteUrl.toPlainRequestBody()
            val notiType: RequestBody? = notiType.value?.name?.toPlainNullableRequestBody()
            val notiDate: RequestBody? = notiDate.value?.toPlainNullableRequestBody()

            val imageMultipartBody: MultipartBody.Part? = itemImage.value?.let { imageUrl ->
                val bitmap = requireNotNull(getBitmapFromURL(imageUrl)) { Timber.e("비트맵 변환 실패") }
                imageFile = requireNotNull(
                    getFileFromBitmap(
                        bitmap,
                        token,
                        application.applicationContext
                    )
                ) { Timber.e("파일 변환 실패") }

                MultipartBody.Part.createFormData(
                    "item_img", imageFile?.name, imageFile!!.asRequestBody()
                )
            }

            val isComplete = wishRepository.uploadWishItem(
                token,
                folderId,
                itemName,
                itemPrice,
                itemUrl,
                notiType,
                notiDate,
                imageMultipartBody
            )
            isCompleteUpload.postValue(isComplete)
        }
        itemRegistrationStatus.postValue(ProcessStatus.IDLE)
    }

    suspend fun uploadWishItemByBasics() {
        if (itemRegistrationStatus.value == ProcessStatus.IN_PROGRESS) return
        if (token == null) return
        val name = itemName.value?.trim() ?: return
        itemRegistrationStatus.value = ProcessStatus.IN_PROGRESS

        val folderId: RequestBody? = folderItem.value?.id?.toString()?.toPlainNullableRequestBody()
        val itemName: RequestBody = name.toPlainRequestBody()
        val itemPrice: RequestBody? = itemPrice.value?.replace(",", "")?.toIntOrNull()?.toString()?.toPlainNullableRequestBody()
        val itemMemo: RequestBody? = getTrimmedMemo(itemMemo.value).toPlainNullableRequestBody()
        val itemUrl: RequestBody? = itemUrl.value.toPlainNullableRequestBody()
        val notiType: RequestBody? = notiType.value?.name?.toPlainNullableRequestBody()
        val notiDate: RequestBody? = notiDate.value?.toPlainNullableRequestBody()

        val imageMultipartBody: MultipartBody.Part = ContentUriRequestBody(
            application.baseContext,
            "item_img",
            selectedGalleryImageUri.value ?: Uri.parse(itemImage.value)
        ).toFormData()

        val isComplete = wishRepository.uploadWishItem(token, folderId, itemName, itemPrice, itemMemo, itemUrl, notiType, notiDate, imageMultipartBody)
        isCompleteUpload.value = isComplete
        itemRegistrationStatus.value = ProcessStatus.IDLE
    }

    private suspend fun updateWishItem(trimmedItemName: String) { // TODO need refactoring, uploadWishItemByBasics()와 합치기
        // 파싱으로 아이템 이미지 불러온 경우 비트맵이미지로 이미지 파일 만들기
        val folderId: RequestBody? =
            (folderItem.value?.id ?: wishItem?.folderId)?.toString()?.toPlainNullableRequestBody()
        val itemName: RequestBody = trimmedItemName.toPlainRequestBody()
        val itemPrice: RequestBody? = itemPrice.value?.replace(",", "")?.toIntOrNull()?.toString()
            ?.toPlainNullableRequestBody()
        val itemMemo: RequestBody? = getTrimmedMemo(itemMemo.value).toPlainNullableRequestBody()
        val itemUrl: RequestBody? = itemUrl.value.toPlainNullableRequestBody()
        val notiType: RequestBody? = notiType.value?.name?.toPlainNullableRequestBody()
        val notiDate: RequestBody? = notiDate.value?.toPlainNullableRequestBody()

        val imageMultipartBody: MultipartBody.Part? =
            if (wishItem?.image != null || wishItem?.imageUrl != null) { // 이미지가 변경되지 않은 경우
                null
            } else if (selectedGalleryImageUri.value != null) {
                ContentUriRequestBody(
                    application.baseContext,
                    "item_img",
                    selectedGalleryImageUri.value!!
                ).toFormData()
            } else if (itemImage.value != null) { // 파싱으로 아이템 이미지 불러온 경우 비트맵이미지로 이미지 파일 만들기
                Timber.e(wishItem.toString())
                val bitmap =
                    requireNotNull(getBitmapFromURL(itemImage.value!!)) { Timber.e("비트맵 변환 실패") }
                imageFile = requireNotNull(
                    getFileFromBitmap(
                        bitmap,
                        token!!,
                        application.applicationContext
                    )
                ) { Timber.e("파일 변환 실패") }
                MultipartBody.Part.createFormData(
                    "item_img", imageFile?.name, imageFile!!.asRequestBody()
                )
            } else {
                null
            }

        val result = wishRepository.updateWishItem(
            requireNotNull(token) { Timber.e("토큰 없음") },
            requireNotNull(itemId) { Timber.e("아이템 아이디 없음") },
            folderId,
            itemName,
            itemPrice,
            itemMemo,
            itemUrl,
            notiType,
            notiDate,
            imageMultipartBody
        )
        _isShownItemNonUpdateDialog.value = result?.second == 404
        isCompleteUpload.value = result?.first
    }

    fun createNewFolder() {
        val folderName = folderName.value?.trim() ?: return
        folderRegistrationStatus.value = ProcessStatus.IN_PROGRESS
        val folder = FolderItem(name = folderName)

        viewModelScope.launch {
            val result = folderRepository.createNewFolder(token!!, folder)
            isCompleteFolderUpload.value = result?.first?.first
            isExistFolderName.value = result?.first?.second == 409
            result?.second?.let { folderId ->
                folderListSquareAdapter.addData(FolderItem(folderId, folderName))
            }
            folderRegistrationStatus.postValue(ProcessStatus.IDLE)
        }
    }

    // TODO need refactoring UseCase로 분리
    private fun fetchFolderList() {
        if (token == null) return
        viewModelScope.launch {
            folderListSquareAdapter.setData(folderRepository.fetchFolderListSummary(token))
        }
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

    /** 입력한 쇼핑몰 링크의 포맷을 검증 후, 유효한 url일 때 아이템 정보 파싱 */
    fun loadWishItemInfo() {
        val url = itemUrlInput.value?.trim()

        val isValid = checkValidationItemUrl(url) // url null 검증 반드시 필요
        isValidItemUrl.value = isValid
        if (!isValid) return

        selectedGalleryImageUri.value = null // 파싱한 이미지를 적용할 것이기 때문에 기존에 선택된 이미지를 제거
        viewModelScope.launch(Dispatchers.IO) {
            getWishItemInfo(url!!)
        }
    }

    /** url 유효성 검증 */
    private fun checkValidationItemUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) {
            return false
        }

        return if (URLUtil.isValidUrl(url) && Patterns.WEB_URL.matcher(url).matches()) {
            itemUrl.value = url
            true
        } else {
            false
        }
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

    private fun initEnabledSaveButton() {
        isEnabledSaveButton.addSource(itemName) { name ->
            combineEnabledSaveButton(name, itemPrice.value)
        }

        isEnabledSaveButton.addSource(itemPrice) { price ->
            combineEnabledSaveButton(itemName.value, price)
        }
    }

    private fun combineEnabledSaveButton(name: String?, price: String?) {
        isEnabledSaveButton.value = !(name.isNullOrBlank() || price.isNullOrBlank())
    }

    fun setWishItem(wishItem: WishItem) {
        this.wishItem = wishItem
        setWishItemInfo()
    }

    fun removeWishItemImage() {
        wishItem?.apply {
            this.imageUrl = null
            this.image = null
        }
    }

    /** UI에 보여질 데이터값 설정 */
    private fun setWishItemInfo() {
        wishItem?.let { item ->
            itemId = item.id
            itemName.value = item.name
            itemImage.value = item.image ?: item.imageUrl
            itemPrice.value = item.price.toString()
            itemMemo.value = item.memo
            itemUrl.value = item.url
            notiType.value = item.notiType
            notiDate.value = item.notiDate
        }
    }

    fun onItemNameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        itemName.value = s.toString()
    }

    fun onItemPriceTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        itemPrice.value = s.toString()
    }

    fun onItemUrlInputTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        itemUrlInput.value = s.toString().trim()
        isValidItemUrl.value = null
    }

    fun onItemMemoTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        itemMemo.value = s.toString()
    }

    fun onNotiTypeValueChanged(picker: NumberPicker, oldVal: Int, newVal: Int) {
        notiTypeVal.value = newVal
    }

    fun onNotiDateValueChanged(picker: NumberPicker, oldVal: Int, newVal: Int) {
        notiDateVal.value = newVal
    }

    fun onNotiHourValueChanged(picker: NumberPicker, oldVal: Int, newVal: Int) {
        notiHourVal.value = newVal
    }

    fun onNotiMinuteValueChanged(picker: NumberPicker, oldVal: Int, newVal: Int) {
        notiMinuteVal.value = newVal
    }

    fun onFolderNameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        folderName.value = s.toString()
        isExistFolderName.value = false
    }

    fun setFolderItem(folder: FolderItem) {
        folderItem.value = folder
    }

    fun setNotiInfo(isNotiSwitchChecked: Boolean, notiType: NotiType?, notiDate: String?) {
        if (isNotiSwitchChecked) {
            this.notiType.value = notiType
            this.notiDate.value = notiDate
        } else {
            this.notiType.value = null
            this.notiDate.value = null
        }
    }

    fun resetNotiInfo() {
        this.notiType.value = null
        this.notiDate.value = null
    }

    fun resetFolderName() {
        folderName.value = null
    }

    fun resetItemUrlInput() {
        itemUrlInput.value = null
    }

    fun setItemUrl(url: String) {
        itemUrl.value = url
    }

    /** 쇼핑몰 링크가 존재하는 아이템을 수정할 경우, 쇼핑몰 링크 EditText에 기존 링크를 보여주기 위함 */
    fun copyItemUrlToInputUrl() {
        itemUrlInput.value = wishItem?.url
    }

    /** 초기화 전 url 검증 실패 시 기존 url로 원상 복구 */
    fun resetValidItemUrl() {
        if (isValidItemUrl.value == false || itemUrlInput.value.isNullOrBlank()) {
            itemUrlInput.value = itemUrl.value
        }

        isValidItemUrl.value = null
    }

    fun setSelectedGalleryImage(imageUri: Uri, imageFile: File) {
        // 갤러리 이미지를 적용할 것이기 때문에 기존에 파싱한 이미지를 제거
        selectedGalleryImageUri.value = imageUri
        itemImage.value =
            null // TODO need refactoring, 아이템 정보 파싱 후 갤러리에서 이미지 선택 하지 않아도 이전에 갤러리에서 이미지를 선택한 적이 있는 경우, 갤러리 이미지가 보이는 버그를 방지하기 위함
        this.imageFile = imageFile
    }

    fun resetCompleteFolderUpload() {
        isCompleteFolderUpload.value = null
    }

    /** 입력된 메모에서 공백을 제거 */
    private fun getTrimmedMemo(memo: String?): String? {
        val trimmedMemo = memo?.trim()
        if (trimmedMemo.isNullOrBlank()) return null
        return trimmedMemo
    }

    private fun getValidUrl(url: String?): String? {
        return if (checkValidationItemUrl(url)) {
            url
        } else {
            getRefinedUrl(url ?: return null)
        }
    }

    /** 쿠팡 > 앱 내 공유하기 버튼 클릭 > 위시보드로 공유할 경우 url 앞에 한글이 붙기 때문에 유효한 url만 떼어내고자 해당 함수에서 url을 가공함 */
    private fun getRefinedUrl(url: String): String? {
        val httpStartIdx = url.indexOf("http")
        if (httpStartIdx == -1) return null
        val refinedUrl = url.substring(httpStartIdx)

        var httpEndIdx = refinedUrl.indexOf(" ")
        if (httpEndIdx == -1) {
            httpEndIdx = refinedUrl.indexOf("\n")
        }

        return if (httpEndIdx == -1) {
            refinedUrl
        } else {
            refinedUrl.substring(0, httpEndIdx)
        }
    }

    fun getItemName(): LiveData<String?> = itemName
    fun getItemImage(): LiveData<String?> = itemImage
    fun getSelectedGalleryUri(): LiveData<Uri?> = selectedGalleryImageUri
    fun getItemPrice(): LiveData<String> = Transformations.map(itemPrice) { price ->
        price?.let {
            applyPriceFormat(price)
        }
    }

    fun getItemImageUrl(): LiveData<String?> = itemImageUrl
    fun getItemUrlInput(): LiveData<String?> = itemUrlInput
    fun getItemMemo(): LiveData<String?> = itemMemo
    fun getFolderItem(): LiveData<FolderItem?> = folderItem
    fun getNotiType(): LiveData<NotiType?> = notiType
    fun getNotiDate(): LiveData<String?> = notiDate

    fun getNotiTypeVal(): LiveData<Int?> = notiTypeVal
    fun getNotiDateVal(): LiveData<Int?> = notiDateVal
    fun getNotiHourVal(): LiveData<Int?> = notiHourVal
    fun getNotiMinuteVal(): LiveData<Int?> = notiMinuteVal

    fun getWishItem(): WishItem? = wishItem
    fun getFolderName(): LiveData<String?> = folderName

    fun getFolderListSquareAdapter(): FolderListAdapter = folderListSquareAdapter

    fun isEnabledSaveButton(): LiveData<Boolean> = isEnabledSaveButton
    fun isCompleteUpload(): LiveData<Boolean?> = isCompleteUpload
    fun isCompleteFolderUpload(): LiveData<Boolean?> = isCompleteFolderUpload

    fun getIsExistFolderName(): LiveData<Boolean?> = isExistFolderName
    fun getIsValidItemUrl(): LiveData<Boolean?> = isValidItemUrl
    fun getRegistrationStatus(): LiveData<ProcessStatus> = itemRegistrationStatus
    fun getFolderRegistrationStatus(): LiveData<ProcessStatus> = folderRegistrationStatus

    companion object {
        private const val TAG = "WishItemRegistrationViewModel"
    }
}