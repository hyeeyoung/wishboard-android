package com.hyeeyoung.wishboard.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.data.model.folder.FolderItem
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.repositories.CartRepository
import com.hyeeyoung.wishboard.domain.repositories.FolderRepository
import com.hyeeyoung.wishboard.domain.repositories.WishRepository
import com.hyeeyoung.wishboard.presentation.base.viewmodel.NetworkViewModel
import com.hyeeyoung.wishboard.presentation.cart.types.CartStateType
import com.hyeeyoung.wishboard.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishListViewModel @Inject constructor(
    private val wishRepository: WishRepository,
    private val cartRepository: CartRepository,
    private val folderRepository: FolderRepository,
) : NetworkViewModel() {
    private val _wishListFetchState = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    val wishListFetchState get() = _wishListFetchState.asStateFlow()
    private val wishList = MutableLiveData<List<WishItem>?>(listOf())
    private val wishListAdapter = WishListAdapter()
    private var _folderItem: FolderItem? = null
    val folderItem get() = _folderItem

    fun fetchWishList() {
        viewModelScope.launch {
            wishRepository.fetchWishList().let {
                wishList.value = it
                wishListAdapter.setData(it)
            }
        }
    }

    fun fetchLatestItem() {
        viewModelScope.launch {
            insertWishItem(wishRepository.fetchLatestWishItem() ?: return@launch)
        }
    }

    fun fetchFolderItems(folderId: Long?) { // TODO need refactoring UseCase로 분리
        if (folderId == null) return
        viewModelScope.launch {
            folderRepository.fetchItemsInFolder(folderId).let { folders ->
                _wishListFetchState.value =
                    if (folders == null) UiState.Error(null) else UiState.Success(true)
                wishList.value = folders
                wishListAdapter.setData(folders)
            }
        }
    }

    fun toggleCartState(position: Int, item: WishItem) {
        if (!isConnected.value) return
        viewModelScope.launch {
            val isInCart = item.cartState == CartStateType.IN_CART.numValue
            val isSuccessful =
                if (isInCart) {
                    cartRepository.removeToCart(item.id ?: return@launch)
                } else {
                    cartRepository.addToCart(item.id ?: return@launch)
                }
            if (!isSuccessful) return@launch

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
        _folderItem = folderItem
    }

    fun getWishList(): LiveData<List<WishItem>?> = wishList
    fun getWishListAdapter(): WishListAdapter = wishListAdapter
}
