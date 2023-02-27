package com.hyeeyoung.wishboard.presentation.wishitem.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.data.model.folder.FolderItem
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.model.WishItemDetail
import com.hyeeyoung.wishboard.domain.repositories.WishRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class WishItemViewModel @Inject constructor(
    private val wishRepository: WishRepository,
) : ViewModel() {
    private val _wishItemThumbnail = MutableLiveData<WishItem>()
    val wishItemThumbnail: LiveData<WishItem> get() = _wishItemThumbnail
    private val _itemDetail = MutableLiveData<WishItemDetail>()
    val itemDetail: LiveData<WishItemDetail> get() = _itemDetail
    private var isCompleteDeletion = MutableLiveData<Boolean>()

    fun fetchWishItemDetail(itemId: Long) {
        viewModelScope.launch {
            _itemDetail.value =
                wishRepository.fetchWishItemDetail(itemId)?.map { it.toWishItemDetail() }
                    ?.get(0)
            generateWishItemThumbnail(itemDetail.value ?: return@launch)
        }
    }

    fun deleteWishItem() {
        val itemId = itemDetail.value?.id ?: return
        viewModelScope.launch {
            isCompleteDeletion.value = wishRepository.deleteWishItem(itemId)
        }
    }

    fun updateWishItemFolder(folder: FolderItem) {
        val item = itemDetail.value?.apply {
            folderId = folder.id
            folderName = folder.name
        } ?: return
        viewModelScope.launch {
            val isComplete =
                wishRepository.updateFolderOfWishItem(
                    itemDetail.value?.id ?: return@launch,
                    folder.id ?: return@launch
                )
            if (isComplete) {
                _itemDetail.value = item
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

    /** 아이템 수정 -> 디테일 화면 복귀 -> 홈화면 복귀 시 홈화면 UI를 업데이트하기 위해 수정된 아이템 정보를 반영한 WishItemThumbnail 객체를 생성함 */
    private fun generateWishItemThumbnail(detail: WishItemDetail) {
        with(detail) {
            _wishItemThumbnail.value =
                WishItem(imageUrl = image, name = name, price = price.toIntOrNull(), id = id)
        }
    }

    fun getIsCompleteDeletion(): LiveData<Boolean> = isCompleteDeletion
}