package com.hyeeyoung.wishboard.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.model.cart.CartItem
import com.hyeeyoung.wishboard.model.cart.CartItemButtonType
import com.hyeeyoung.wishboard.repository.cart.CartRepository
import com.hyeeyoung.wishboard.util.prefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val token = prefs?.getUserToken()
    private val cartList = MutableLiveData<ArrayList<CartItem>>(arrayListOf())

    init {
        fetchCartList()
    }

    private fun fetchCartList() {
        if (token == null) return
        viewModelScope.launch {
            val items = cartRepository.fetchCartList(token)
            cartList.postValue(items as ArrayList<CartItem>?)
        }
    }

    fun removeToCart(itemId: Long) {
        if (token == null) return
        viewModelScope.launch {
            cartRepository.removeToCart(token, itemId)
        }
    }

    fun updateToCart(items: ArrayList<CartItem>) {
        if (token == null) return
        viewModelScope.launch {
            cartRepository.updateToCart(token, items)
        }
    }

    //TODO 수정 필요 (변화 없음)
    fun changeCountControl(item: CartItem, position: Int, viewType: CartItemButtonType) {
        if (item.wishItem.price == 0) return //TODO 예외처리 필요
        when (viewType) {
            CartItemButtonType.VIEW_TYPE_PLUS -> {
                item.apply {
                    item.cartItemInfo.count.plus(1)
                    item.wishItem.price?.times(item.cartItemInfo.count)
                }
                cartList.value?.set(position, item)
            }
            CartItemButtonType.VIEW_TYPE_MINUS -> {
                if (item.cartItemInfo.count == 0) return //TODO count가 0이면 더이상 내릴 수 없도록 예외처리 필요
                item.apply {
                    item.cartItemInfo.count.minus(1)
                    item.wishItem.price?.times(item.cartItemInfo.count)
                }
                cartList.value?.set(position, item)
            }
        }
    }

    fun getTotalPrice() = Transformations.map(cartList) { calculateTotalPrice(it) } //Note 변경되지 않음

    private fun calculateTotalPrice(cartList: List<CartItem>): Int {
        var sum = 0
        cartList.forEach {
            sum += it.wishItem.price ?: 0
        }
        return sum
    }

    fun getTotalCount() = Transformations.map(cartList) { calculateTotalCount(it) } //Note 변경되지 않음

    private fun calculateTotalCount(cartList: List<CartItem>): Int {
        var sum = 0
        cartList.forEach {
            sum += it.cartItemInfo.count
        }
        return sum
    }

    fun getCartList(): LiveData<ArrayList<CartItem>?> = cartList


    companion object {
        private val TAG = "CartViewModel"
    }
}