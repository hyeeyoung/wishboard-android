package com.hyeeyoung.wishboard.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.model.wish.WishItemInfo
import com.hyeeyoung.wishboard.repository.cart.CartRepository
import com.hyeeyoung.wishboard.repository.wish.WishRepository
import com.hyeeyoung.wishboard.util.prefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishViewModel @Inject constructor(
    private val wishRepository: WishRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private var wishList = MutableLiveData<List<WishItem>>(arrayListOf())
    private var wishItem = MutableLiveData<WishItemInfo>()
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

    fun fetchWishItem(itemId: Long) {
        if (token == null) return
        viewModelScope.launch {
            val item = wishRepository.fetchWishItem(itemId)
            wishItem.postValue(item)
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

    fun getWishList(): LiveData<List<WishItem>?> = wishList
    fun getWishItem(): LiveData<WishItemInfo?> = wishItem

    companion object {
        private val TAG = "WishViewModel"
    }
}