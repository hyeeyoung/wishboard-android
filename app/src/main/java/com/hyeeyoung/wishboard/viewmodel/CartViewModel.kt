package com.hyeeyoung.wishboard.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.model.cart.CartItem
import com.hyeeyoung.wishboard.model.cart.CartItemButtonType
import com.hyeeyoung.wishboard.repository.cart.CartRepository
import com.hyeeyoung.wishboard.util.prefs
import com.hyeeyoung.wishboard.view.cart.adapters.CartListAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val application: Application,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val token = prefs?.getUserToken()
    private val cartList = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    private val cartListAdapter = CartListAdapter(application)
    private var totalPrice = MutableLiveData<Int>()

    init {
        fetchCartList()
    }

    private fun fetchCartList() {
        if (token == null) return
        viewModelScope.launch {
            val items = cartRepository.fetchCartList(token)
            cartListAdapter.setData(items ?: return@launch)
            cartList.postValue(items as MutableList<CartItem>)
        }
    }

    fun removeToCart(itemId: Long) {
        if (token == null) return
        viewModelScope.launch {
            cartRepository.removeToCart(token, itemId)
        }
    }

    fun controlItemCount(item: CartItem, position: Int, viewType: CartItemButtonType) {
        if (item.wishItem.price == 0) return //TODO 예외처리 필요
        item.also {
            when (viewType) {
                CartItemButtonType.VIEW_TYPE_PLUS -> {
                    it.cartItemInfo.count += 1
                }
                CartItemButtonType.VIEW_TYPE_MINUS -> {
                    if (item.cartItemInfo.count == 0) return //TODO count가 0이면 더이상 내릴 수 없도록 예외처리 필요
                    it.cartItemInfo.count -= 1
                }
            }
            updateCartInfo(item, position)
        }
    }

    private fun updateCartInfo(item: CartItem, position: Int) {
        if (token == null) return
        viewModelScope.launch {
            val isSuccessful = cartRepository.updateCartInfo(token, item)
            if (!isSuccessful) return@launch // TODO 장바구니 업데이트 실패 시 예외 처리 필요

            cartList.value?.set(position, item)
            cartListAdapter.updateItem(position, item)
            calculateTotalPrice()
        }
    }

    private fun calculateTotalPrice() {
        totalPrice.value = cartList.value?.sumOf {
            (it.wishItem.price ?: 0) * it.cartItemInfo.count
        }
    }

    fun getCartList(): LiveData<MutableList<CartItem>?> = cartList
    fun getCartListAdapter(): CartListAdapter = cartListAdapter
    fun getTotalPrice(): LiveData<Int> = totalPrice

    companion object {
        private const val TAG = "CartViewModel"
    }
}