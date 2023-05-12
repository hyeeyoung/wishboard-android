package com.hyeeyoung.wishboard.presentation.cart

import androidx.lifecycle.*
import com.hyeeyoung.wishboard.data.model.cart.CartItem
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.repositories.CartRepository
import com.hyeeyoung.wishboard.presentation.cart.types.CartItemButtonType
import com.hyeeyoung.wishboard.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _cartFetchState = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    val cartFetchState get() = _cartFetchState.asStateFlow()
    private val _cartList = MutableLiveData<List<CartItem>?>(listOf())
    val cartList: LiveData<List<CartItem>?> get() = _cartList
    val cartListAdapter = CartListAdapter()
    private val _totalPrice = MediatorLiveData<Int>()
    val totalPrice: LiveData<Int> get() = _totalPrice

    init {
        calculateTotalPrice()
    }

    fun fetchCartList() {
        viewModelScope.launch {
            _cartList.value = cartRepository.fetchCartList()
            _cartFetchState.value =
                if (_cartList.value != null) UiState.Success(true)
                else UiState.Error(null)
            cartListAdapter.setData(_cartList.value ?: return@launch)
        }
    }

    fun removeToCart(itemId: Long) {
        viewModelScope.launch {
            val isSuccessful = cartRepository.removeToCart(itemId)
            if (isSuccessful) removeToCartList(itemId)
        }
    }

    fun controlItemCount(item: CartItem, viewType: CartItemButtonType) {
        if (item.wishItem.price == 0) return // TODO 예외처리 필요
        item.also {
            when (viewType) {
                CartItemButtonType.VIEW_TYPE_PLUS -> {
                    it.cartItemInfo.count += 1
                }
                CartItemButtonType.VIEW_TYPE_MINUS -> {
                    if (item.cartItemInfo.count <= 1) return
                    it.cartItemInfo.count -= 1
                }
                else -> {}
            }
            updateCartItemCount(item)
        }
    }

    private fun updateCartItemCount(item: CartItem) {
        viewModelScope.launch {
            val isSuccessful = cartRepository.updateCartItemCount(item)
            if (!isSuccessful) return@launch // TODO 장바구니 업데이트 실패 시 예외 처리 필요

            cartListAdapter.updateItem(item)
            _cartList.value = cartListAdapter.getData()
        }
    }

    /** 장바구니에서 아이템 수정할 경우 ui 업데이트 */
    fun updateCartItem(item: WishItem) {
        cartListAdapter.updateItem(item)
        _cartList.value = cartListAdapter.getData()
    }

    /** 장바구니에서 아이템 삭제할 경우 ui 업데이트 */
    fun removeToCartList(itemId: Long) {
        cartListAdapter.removeItem(itemId)
        _cartList.value = cartListAdapter.getData()
    }

    private fun calculateTotalPrice() {
        _totalPrice.addSource(_cartList) {
            _totalPrice.value = _cartList.value?.sumOf {
                (it.wishItem.price ?: 0) * it.cartItemInfo.count
            }
        }
    }
}