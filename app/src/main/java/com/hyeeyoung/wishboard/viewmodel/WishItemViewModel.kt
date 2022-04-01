package com.hyeeyoung.wishboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.repository.wish.WishRepository
import com.hyeeyoung.wishboard.service.AWSS3Service
import com.hyeeyoung.wishboard.util.prefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.net.URISyntaxException
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class WishItemViewModel @Inject constructor(
    private val wishRepository: WishRepository,
) : ViewModel() {
    private val token = prefs?.getUserToken()

    private var wishItem = MutableLiveData<WishItem>()
    private var isCompleteDeletion = MutableLiveData<Boolean>()
    private var isCompleteFolderUpdate = MutableLiveData<Boolean>()

    fun deleteWishItem() {
        if (token == null) return
        val itemId = wishItem.value?.id ?: return
        viewModelScope.launch {
            isCompleteDeletion.value = wishRepository.deleteWishItem(token, itemId)
            // 아이템이 삭제 완료된 경우, s3에서도 이미지 객체 삭제
            if (isCompleteDeletion.value == true) {
                AWSS3Service().removeImageUrl(wishItem.value!!.image ?: return@launch)
            }
        }
    }

    fun setWishItem(item: WishItem) { // TODO refactoring
        if (item.imageUrl == null && item.image != null) {
            viewModelScope.launch {
                item.imageUrl = AWSS3Service().getImageUrl(item.image)
                wishItem.value = item // 이미지 다운로드까지 시간이 소요됨에 따라 if 문 밖에서 해당 코드 실행할 경우, imageUrl이 초기화 되기 전에 wishItem을 초기화함
            }
        } else {
            wishItem.value = item
        }
    }

    fun updateWishItemFolder(folder: FolderItem) {
        if (token == null) return
        val itemId = wishItem.value?.id ?: return
        val item = wishItem.value?.apply {
            folderId = folder.id
            folderName = folder.name
        } ?: return
        viewModelScope.launch {
            // TODO 폴더 지정 api 생성되면 폴더만 업데이트 되도록 변경 예정. 현재는 아이템 정보 모두 업데이트 됨
            isCompleteFolderUpdate.value = wishRepository.updateWishItem(token, itemId, item)
            // 아이템이 삭제 완료된 경우, s3에서도 이미지 객체 삭제
            if (isCompleteFolderUpdate.value == true) {
                wishItem.postValue(item)
            }
        }
    }

    /** url에서 도메인명을 추출 */
    fun getDomainName(url: String): String? {
        return try {
            val domain = URL(url).host

            if (domain.startsWith("www.")) {
                domain.substring(4)
            } else {
                domain
            }
        } catch (e: URISyntaxException) {
            null
        }
    }

    fun getWishItem(): LiveData<WishItem> = wishItem
    fun getIsCompleteDeletion(): LiveData<Boolean> = isCompleteDeletion

    companion object {
        private const val TAG = "WishItemViewModel"
    }
}