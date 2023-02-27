package com.hyeeyoung.wishboard.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.WishBoardApp
import com.hyeeyoung.wishboard.data.model.folder.FolderItem
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.repositories.CartRepository
import com.hyeeyoung.wishboard.domain.repositories.FolderRepository
import com.hyeeyoung.wishboard.domain.repositories.WishRepository
import com.hyeeyoung.wishboard.presentation.cart.types.CartStateType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishListViewModel @Inject constructor(
    private val wishRepository: WishRepository,
    private val cartRepository: CartRepository,
    private val folderRepository: FolderRepository,
) : ViewModel() {
    private val token = WishBoardApp.prefs.getUserToken()

    private val wishList = MutableLiveData<List<WishItem>?>(listOf())
    private val wishListAdapter = WishListAdapter()
    private var folderItem: FolderItem? = null

    fun fetchWishList() {
        if (token == null) return
        viewModelScope.launch {
            wishRepository.fetchWishList(token).let {
                wishList.postValue(it)
                wishListAdapter.setData(it)
            }
        }
    }

    fun fetchLatestItem() {
        if (token == null) return
        viewModelScope.launch {
            insertWishItem(wishRepository.fetchLatestWishItem(token) ?: return@launch)
        }
    }

    fun fetchFolderItems(folderId: Long?) { // TODO need refactoring UseCase로 분리
        if (token == null || folderId == null) return
        viewModelScope.launch {
            folderRepository.fetchItemsInFolder(token, folderId).let { folders ->
                wishList.postValue(folders)
                wishListAdapter.setData(folders)
            }
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

    private fun insertWishItem(wishItem: WishItem) {
        wishListAdapter.insertData(wishItem)
        wishList.value = wishListAdapter.getData()
    }

    fun updateWishItem(position: Int, wishItem: WishItem) {
        wishListAdapter.updateData(position, wishItem)
    }

    fun deleteWishItem(position: Int, wishItem: WishItem) {
        wishListAdapter.deleteData(position, wishItem)
        wishList.value = wishListAdapter.getData()
    }

    fun setFolderItem(folderItem: FolderItem) {
        this.folderItem = folderItem
    }

    fun getWishList(): LiveData<List<WishItem>?> = wishList
    fun getWishListAdapter(): WishListAdapter = wishListAdapter
    fun getFolderItem(): FolderItem? = folderItem
}
