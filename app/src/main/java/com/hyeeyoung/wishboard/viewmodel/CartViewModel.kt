package com.hyeeyoung.wishboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.model.CartItem
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
    private var cartItems = MutableLiveData<ArrayList<CartItem>>(arrayListOf())

    init {
        fetchCart()
    }

    private fun fetchCart() {
        if (token == null) return
        viewModelScope.launch {
            val items = cartRepository.fetchCart(token) // TODO userId 가져오기
            cartItems.postValue(items)
        }
    }

    fun addToCart(itemId: Int) {
        if (token == null) return
        viewModelScope.launch {
            cartRepository.addToCart(token, itemId)
        }
    }

    fun getCartItems(): LiveData<ArrayList<CartItem>?> = cartItems

    companion object {
        private val TAG = "WishViewModel"
    }
}