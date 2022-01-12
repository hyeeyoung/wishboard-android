package com.hyeeyoung.wishboard.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.repository.cart.CartRepository
import com.hyeeyoung.wishboard.repository.wish.WishRepository
import com.hyeeyoung.wishboard.util.prefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishViewModel @Inject constructor( // TODO WishListViewModel 및 WishItemViewModel로 구분하여 정리 필요
    private val wishRepository: WishRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private var wishList = MutableLiveData<List<WishItem>>(arrayListOf())
    private var wishItem = MutableLiveData<WishItem>()
    private val token = prefs?.getUserToken()

    init {
        fetchWishList()
    }

    fun fetchWishList() {
        if (token == null) return
        Log.d(TAG, "token: $token")
        viewModelScope.launch {
            val items = wishRepository.fetchWishList(token)
            wishList.postValue(items)
        }
    }

    fun addToCart(itemId: Long) {
        if (token == null) return
        viewModelScope.launch {
            cartRepository.addToCart(token, itemId)
        }
    }

    fun removeToCart(itemId: Long) {
        if (token == null) return
        viewModelScope.launch {
            cartRepository.removeToCart(token, itemId)
        }
    }

    fun setWishItem(wishItem: WishItem) {
        this.wishItem.value = wishItem
    }

    fun getWishList(): LiveData<List<WishItem>?> = wishList
    fun getWishItem(): LiveData<WishItem> = wishItem

    companion object {
        private val TAG = "WishViewModel"
    }
}
