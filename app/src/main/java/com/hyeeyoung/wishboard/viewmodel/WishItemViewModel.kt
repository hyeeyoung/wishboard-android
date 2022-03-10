package com.hyeeyoung.wishboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.repository.wish.WishRepository
import com.hyeeyoung.wishboard.service.AWSS3Service
import com.hyeeyoung.wishboard.util.prefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishItemViewModel @Inject constructor(
    private val wishRepository: WishRepository,
) : ViewModel() {
    private val token = prefs?.getUserToken()

    private var wishItem = MutableLiveData<WishItem>()
    private var isCompleteDeletion = MutableLiveData<Boolean>()

    fun deleteWishItem() {
        if (token == null) return
        val itemId = wishItem.value?.id ?: return

        viewModelScope.launch {
            isCompleteDeletion.value = wishRepository.deleteWishItem(token, itemId)
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

    fun getWishItem(): LiveData<WishItem> = wishItem
    fun getIsCompleteDeletion(): LiveData<Boolean> = isCompleteDeletion

    companion object {
        private const val TAG = "WishItemViewModel"
    }
}