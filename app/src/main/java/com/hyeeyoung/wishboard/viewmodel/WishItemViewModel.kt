package com.hyeeyoung.wishboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hyeeyoung.wishboard.model.wish.WishItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WishItemViewModel @Inject constructor() : ViewModel() {
    private var wishItem = MutableLiveData<WishItem>()

    fun setWishItem(wishItem: WishItem) {
        this.wishItem.value = wishItem
    }

    fun getWishItem(): LiveData<WishItem> = wishItem

    companion object {
        private const val TAG = "WishItemViewModel"
    }
}