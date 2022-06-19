package com.hyeeyoung.wishboard.presentation.cart

import androidx.lifecycle.*
import com.hyeeyoung.wishboard.WishBoardApp
import com.hyeeyoung.wishboard.data.model.cart.CartItem
import com.hyeeyoung.wishboard.presentation.cart.types.CartItemButtonType
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.repositories.CartRepository
import com.hyeeyoung.wishboard.data.services.AWSS3Service
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val token = WishBoardApp.prefs.getUserToken()
    private val cartList = MutableLiveData<List<CartItem>?>(listOf())
    private val cartListAdapter = CartListAdapter()
    private var totalPrice = MediatorLiveData<Int>()

    init {
        calculateTotalPrice()
        fetchCartList()
    }

    private fun fetchCartList() {
        if (token == null) return
        viewModelScope.launch {
            var items: List<CartItem>?
            withContext(Dispatchers.IO) {
                items = cartRepository.fetchCartList(token)
                items?.forEach { item ->
                    item.wishItem.image?.let {
                        item.wishItem.imageUrl = AWSS3Service().getImageUrl(it)
                    }
                }
                cartList.postValue(items)
            }
            withContext(Dispatchers.Main) {
                cartListAdapter.setData(items ?: return@withContext)
            }
        }
    }

    fun removeToCart(itemId: Long, position: Int) {
        if (token == null) return
        viewModelScope.launch {
            val isSuccessful = cartRepository.removeToCart(token, itemId)
            if (!isSuccessful) return@launch

            cartListAdapter.removeItem(position)
            cartList.postValue(cartListAdapter.getData())
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
            updateCartItemCount(item, position)
        }
    }

    private fun updateCartItemCount(item: CartItem, position: Int) {
        if (token == null) return
        viewModelScope.launch {
            val isSuccessful = cartRepository.updateCartItemCount(token, item)
            if (!isSuccessful) return@launch // TODO 장바구니 업데이트 실패 시 예외 처리 필요

            cartListAdapter.updateItem(position, item)
            cartList.postValue(cartListAdapter.getData())
        }
    }

    /** 장바구니에서 아이템 수정할 경우 ui 업데이트 */
    fun updateCartItem(position: Int, item: WishItem) {
        cartListAdapter.updateItem(position, item)
        cartList.postValue(cartListAdapter.getData())
    }

    /** 장바구니에서 아이템 삭제할 경우 ui 업데이트 */
    fun deleteCartItem(position: Int) {
        cartListAdapter.removeItem(position)
        cartList.postValue(cartListAdapter.getData())
    }

    private fun calculateTotalPrice() {
        totalPrice.addSource(cartList) {
            totalPrice.value = cartList.value?.sumOf {
                (it.wishItem.price ?: 0) * it.cartItemInfo.count
            }
        }
    }

    fun getCartList(): LiveData<List<CartItem>?> = cartList
    fun getCartListAdapter(): CartListAdapter = cartListAdapter
    fun getTotalPrice(): LiveData<Int> = totalPrice

    companion object {
        private const val TAG = "CartViewModel"
    }
}