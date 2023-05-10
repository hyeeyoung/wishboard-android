package com.hyeeyoung.wishboard.presentation.wishitem.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.data.model.folder.FolderItem
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.model.WishItemDetail
import com.hyeeyoung.wishboard.domain.repositories.WishRepository
import com.hyeeyoung.wishboard.presentation.base.viewmodel.NetworkViewModel
import com.hyeeyoung.wishboard.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class WishItemViewModel @Inject constructor(
    private val wishRepository: WishRepository,
) : NetworkViewModel() {
    private val _wishDetailFetchState = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    val wishDetailFetchState get() = _wishDetailFetchState.asStateFlow()
    private val _wishItemThumbnail = MutableLiveData<WishItem>()
    val wishItemThumbnail: LiveData<WishItem> get() = _wishItemThumbnail
    private val _itemDetail = MutableLiveData<WishItemDetail>()
    private val _itemImage = MutableStateFlow<String?>(null)
    val itemImage get() = _itemImage.asStateFlow()
    val itemDetail: LiveData<WishItemDetail> get() = _itemDetail
    private var isCompleteDeletion = MutableLiveData<Boolean>()

    fun fetchWishItemDetail(itemId: Long) {
        viewModelScope.launch {
            _itemDetail.value =
                wishRepository.fetchWishItemDetail(itemId)?.map { it.toWishItemDetail(it) }
                    ?.get(0)
            _wishDetailFetchState.value = if (itemDetail.value == null) UiState.Error(null) else UiState.Success(true)
            _itemImage.value = itemDetail.value?.image
            generateWishItemThumbnail(itemDetail.value ?: return@launch)
        }
    }

    fun deleteWishItem() {
        if (!isConnected.value) return
        val itemId = itemDetail.value?.id ?: return
        viewModelScope.launch {
            isCompleteDeletion.value = wishRepository.deleteWishItem(itemId)
        }
    }

    fun updateWishItemFolder(folder: FolderItem) {
        if (!isConnected.value) return
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

    fun requestFetchWishDetail() {
        _wishDetailFetchState.value = UiState.Loading
    }

    fun getIsCompleteDeletion(): LiveData<Boolean> = isCompleteDeletion
}