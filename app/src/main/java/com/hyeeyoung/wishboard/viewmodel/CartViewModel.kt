package com.hyeeyoung.wishboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.model.cart.CartItem
import com.hyeeyoung.wishboard.repository.cart.CartRepository
import com.hyeeyoung.wishboard.util.prefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
): ViewModel() {
    private val token = prefs?.getUserToken()
    private var cartList = MutableLiveData<List<CartItem>>(arrayListOf())

    init {
        fetchCartList()
    }

    private fun fetchCartList() {
        if (token == null) return
        viewModelScope.launch {
            val items = cartRepository.fetchCartList(token)
            cartList.postValue(items)
        }
    }

    fun removeToCart(itemId: Long) {
        if (token == null) return
        viewModelScope.launch {
            cartRepository.removeToCart(token, itemId)
        }
    }

    fun getCartList(): LiveData<List<CartItem>?> = cartList

    companion object {
        private val TAG = "WishViewModel"
    }
}