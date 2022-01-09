package com.hyeeyoung.wishboard.viewmodel

import android.app.Application
import android.content.ContentResolver
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
import com.hyeeyoung.wishboard.repository.common.GalleryPagingDataSource
import com.hyeeyoung.wishboard.repository.common.GalleryRepository
import com.hyeeyoung.wishboard.repository.wish.WishRepository
import com.hyeeyoung.wishboard.util.getTimestamp
import com.hyeeyoung.wishboard.util.prefs
import com.hyeeyoung.wishboard.util.safeLet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
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

    private val token = prefs?.getUserToken()

    suspend fun uploadWishItemByLinkSharing() {
        if (token == null) return
        // TODO itemImage 없어도 업로드 되도록 수정
        // TODO 가격 데이터에 ',' 있는 경우 문자열 처리 필요
        val price = if (itemPrice.value?.isDigitsOnly() == true) {
            itemPrice.value!!.toInt()
        } else {
            null
        }
        safeLet(itemName.value?.trim(), itemImage.value, itemUrl.value) { name, image, url ->
            val wishItemRegistrationInfo =
                WishItemRegistrationInfo(name, image, price, url, itemMemo.value?.trim())
            val isComplete = wishRepository.uploadWishItem(token, wishItemRegistrationInfo)
            isCompleteUpload.postValue(isComplete)
        }
    }

    suspend fun uploadWishItemByBasics() {
        if (token == null) return
        val price = if (itemPrice.value?.isDigitsOnly() == true) {
            itemPrice.value!!.toInt()
        } else {
            null
        }
        // TODO 갤러리 이미지 가져오기 구현 후 삭제
        // TODO 가격이 존재하지 않는 경우 업로드 실패 -> 서버 코드 확인 필요
        itemImage.value =
            "https://image.msscdn.net/images/goods_img/20210823/2081554/2081554_2_500.jpg"
        safeLet(itemName.value?.trim(), itemImage.value) { name, image ->
            val wishItemRegistrationInfo =
                WishItemRegistrationInfo(name, image, price, itemUrl.value, itemMemo.value?.trim())
            val isComplete = wishRepository.uploadWishItem(token, wishItemRegistrationInfo)
            isCompleteUpload.postValue(isComplete)
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

    fun createFile(): File? {
        val token = token ?: return null
        val file = File(application.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "wishtem")
        if (!file.exists()) {
            file.mkdirs()
        }

        val timestamp = getTimestamp()
        val fileName = "${token}_$timestamp.jpg"
        return File(file.absoluteFile, fileName)
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

    fun setCompletedUpload(isCompleted: Boolean?) {
        isCompleteUpload.value = isCompleted
    }

    fun setSelectedGalleryImageUri(imageUri: Uri?) {
        selectedGalleryImageUri.value = imageUri
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