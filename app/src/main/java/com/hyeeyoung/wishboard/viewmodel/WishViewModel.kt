package com.hyeeyoung.wishboard.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.model.wish.WishItemInfo
import com.hyeeyoung.wishboard.repository.wish.WishRepository
import com.hyeeyoung.wishboard.util.prefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishViewModel @Inject constructor(
    private val wishRepository: WishRepository,
): ViewModel() {
    private var wishItems = MutableLiveData<List<WishItem>>(arrayListOf())
    private var wishItem = MutableLiveData<WishItemInfo>()
    private val token = prefs?.getUserToken()

    init {
        fetchWishList()
    }

    private fun fetchWishList() {
        Log.d(TAG, "fetchWishList: $token")
        if (token == null) return
        viewModelScope.launch {
            val items = wishRepository.fetchWishList(token)
            wishItems.postValue(items)
        }
    }

    fun fetchWishItem(itemId: Int) {
        Log.d(TAG, "fetchWishItem: $token")
        if (token == null) return
        viewModelScope.launch {
            val item = wishRepository.fetchWishItem(token, itemId)
            wishItem.postValue(item)
        }
    }

    fun getWishItems(): LiveData<List<WishItem>?> = wishItems
    fun getWishItem(): LiveData<WishItemInfo?> = wishItem

    companion object {
        private val TAG = "WishViewModel"
    }
}