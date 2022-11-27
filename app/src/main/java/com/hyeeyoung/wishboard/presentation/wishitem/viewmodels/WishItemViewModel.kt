package com.hyeeyoung.wishboard.presentation.wishitem.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.WishBoardApp
import com.hyeeyoung.wishboard.data.model.folder.FolderItem
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.repositories.WishRepository
import com.hyeeyoung.wishboard.data.services.AWSS3Service
import com.hyeeyoung.wishboard.domain.entity.WishItemDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class WishItemViewModel @Inject constructor(
    private val wishRepository: WishRepository,
) : ViewModel() {
    private val token = WishBoardApp.prefs.getUserToken()

    private var wishItem = MutableLiveData<WishItem>()
    private var _itemDetail = MutableLiveData<WishItemDetail>()
    val itemDetail: LiveData<WishItemDetail> get() = _itemDetail
    private var isCompleteDeletion = MutableLiveData<Boolean>()

    fun fetchWishItemDetail(itemId: Long) {
        if (token == null) return
        viewModelScope.launch {
            Timber.d(wishRepository.fetchWishItemDetail(token, itemId)?.get(0).toString())
            _itemDetail.value = wishRepository.fetchWishItemDetail(token, itemId)?.map { it.toWishItemDetail(it) }?.get(0)
        }
    }

    fun deleteWishItem() {
        if (token == null) return
        val itemId = wishItem.value?.id ?: return
        viewModelScope.launch {
            isCompleteDeletion.value = wishRepository.deleteWishItem(token, itemId)
            // 아이템이 삭제 완료된 경우, s3에서도 이미지 객체 삭제
            if (isCompleteDeletion.value == true) {
                AWSS3Service().removeImageUrl(wishItem.value!!.imageUrl ?: return@launch)
            }
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
            val isComplete =
                wishRepository.updateFolderOfWishItem(token, itemId, folder.id ?: return@launch)
            if (isComplete) {
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
        } catch (e: Exception) {
            null
        }
    }

    fun setItemDetail(detail: WishItemDetail) {
        _itemDetail.value = detail
    }

    fun getWishItem(): LiveData<WishItem> = wishItem
    fun getIsCompleteDeletion(): LiveData<Boolean> = isCompleteDeletion
}