package com.hyeeyoung.wishboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.repository.wish.WishRepository
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

    fun setWishItem(wishItem: WishItem) {
        this.wishItem.value = wishItem
    }

    fun getWishItem(): LiveData<WishItem> = wishItem
    fun getIsCompleteDeletion(): LiveData<Boolean> = isCompleteDeletion

    companion object {
        private const val TAG = "WishItemViewModel"
    }
}