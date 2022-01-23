package com.hyeeyoung.wishboard.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.model.cart.CartStateType
import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.remote.AWSS3Service
import com.hyeeyoung.wishboard.repository.cart.CartRepository
import com.hyeeyoung.wishboard.repository.wish.WishRepository
import com.hyeeyoung.wishboard.util.prefs
import com.hyeeyoung.wishboard.view.wish.list.adapters.WishListAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishViewModel @Inject constructor(
    // TODO WishListViewModel로 이름 변경
    private val application: Application,
    private val wishRepository: WishRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private var wishList = MutableLiveData<MutableList<WishItem>>(mutableListOf())
    private var wishItem = MutableLiveData<WishItem>()
    private val wishListAdapter = WishListAdapter(application)

    private val token = prefs?.getUserToken()

    init {
        fetchWishList()
    }

    fun fetchWishList() {
        if (token == null) return
        val wishItems = mutableListOf<WishItem>()
        viewModelScope.launch(Dispatchers.IO) {
            val items = wishRepository.fetchWishList(token) // TODO refactoring
            items?.forEach { item ->
                val imageUrl = AWSS3Service().getImageUrl(item.image)
                imageUrl?.let { url ->
                    wishItems.add(WishItem.from(url, item))
                }
            }
            wishList.postValue(wishItems)
        }
    }

    fun toggleCartState(position: Int, item: WishItem) {
        if (token == null) return
        viewModelScope.launch {
            val isInCart = item.cartState == CartStateType.IN_CART.numValue
            val isSuccessful =
                if (isInCart) {
                    cartRepository.removeToCart(token, item.id ?: return@launch)
                } else {
                    cartRepository.addToCart(token, item.id ?: return@launch)
                }
            if (!isSuccessful) return@launch // TODO 네트워크 연결 실패, 그 외 나머지 예외 처리 -> 스낵바 띄우기

            item.also { wishItem ->
                wishItem.cartState = toggleCartState(wishItem.cartState)
                wishList.value?.set(position, wishItem)
                wishListAdapter.updateData(position, wishItem)
            }
        }
    }

    private fun toggleCartState(stateValue: Int?): Int {
        return if (stateValue == CartStateType.IN_CART.numValue) {
            CartStateType.NOT_IN_CART.numValue
        } else {
            CartStateType.IN_CART.numValue
        }
    }

    fun setWishItem(wishItem: WishItem) { // TODO WishItemViewModel로 이동
        this.wishItem.value = wishItem
    }

    fun getWishList(): LiveData<MutableList<WishItem>?> = wishList
    fun getWishItem(): LiveData<WishItem> = wishItem
    fun getWishListAdapter(): WishListAdapter = wishListAdapter

    companion object {
        private val TAG = "WishViewModel"
    }
}
