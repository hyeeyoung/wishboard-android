package com.hyeeyoung.wishboard.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.repository.cart.CartRepository
import com.hyeeyoung.wishboard.repository.wish.WishRepository
import com.hyeeyoung.wishboard.util.prefs
import com.hyeeyoung.wishboard.view.wish.list.adapters.WishListAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
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
        Log.d(TAG, "token: $token")
        viewModelScope.launch {
            val items = wishRepository.fetchWishList(token)
            wishList.postValue(items?.toMutableList())
        }
    }

    fun addToCart(position: Int, item: WishItem) {
        if (token == null) return
        viewModelScope.launch {
            val isSuccessful = cartRepository.addToCart(token, item.id ?: return@launch)
            if (!isSuccessful) return@launch // TODO 네트워크 연결 실패, 그 외 나머지 예외 처리 -> 스낵바 띄우기
            item.also { wishItem ->
                wishItem.cartId = wishItem.id
                wishList.value?.set(position, wishItem)
                wishListAdapter.updateData(position, wishItem)
            }
        }
    }

    fun removeToCart(position: Int, item: WishItem) {
        if (token == null) return
        viewModelScope.launch {
            val isSuccessful = cartRepository.removeToCart(token, item.id ?: return@launch)
            if (!isSuccessful) return@launch // TODO 예외 처리 필요, 스낵바 띄우기
            item.also { wishItem ->
                wishItem.cartId = null // TODO 서버에서 cartId(-> isAddedCart) 값의 null 여부에 따라 0, 1(Boolean)로 수정할 수 있는지 논의 필요
                wishList.value?.set(position, wishItem)
                wishListAdapter.updateData(position, wishItem)
            }
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
