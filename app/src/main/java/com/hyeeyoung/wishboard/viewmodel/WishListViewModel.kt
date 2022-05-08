package com.hyeeyoung.wishboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.WishBoardApp
import com.hyeeyoung.wishboard.model.cart.CartStateType
import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.repository.cart.CartRepository
import com.hyeeyoung.wishboard.repository.folder.FolderRepository
import com.hyeeyoung.wishboard.repository.wish.WishRepository
import com.hyeeyoung.wishboard.service.AWSS3Service
import com.hyeeyoung.wishboard.view.wish.list.adapters.WishListAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
            var items: List<WishItem>?
            withContext(Dispatchers.IO) {
                items = wishRepository.fetchWishList(token)
                items?.forEach { item ->
                    item.image?.let { item.imageUrl = AWSS3Service().getImageUrl(it) }
                }
            }
            withContext(Dispatchers.Main) {
                wishList.postValue(items)
                wishListAdapter.setData(items)
            }
        }
    }

    fun fetchLatestItem() {
        if (token == null) return
        viewModelScope.launch {
            var item: WishItem? = null
            withContext(Dispatchers.IO) {
                item = wishRepository.fetchLatestWishItem(token) ?: return@withContext
                item!!.image?.let { item!!.imageUrl = AWSS3Service().getImageUrl(it) }
            }
            withContext(Dispatchers.Main) {
                insertWishItem(item ?: return@withContext)
            }
        }
    }

    fun fetchFolderItems(folderId: Long?) { // TODO need refactoring
        if (token == null || folderId == null) return
        viewModelScope.launch {
            var items: List<WishItem>?
            withContext(Dispatchers.IO) {
                items = folderRepository.fetchItemsInFolder(token, folderId)
                items?.forEach { item ->
                    item.image?.let { item.imageUrl = AWSS3Service().getImageUrl(it) }
                }
            }
            withContext(Dispatchers.Main) {
                wishList.postValue(items)
                wishListAdapter.setData(items)
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

    companion object {
        private const val TAG = "WishListViewModel"
    }
}
