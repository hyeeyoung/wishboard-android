package com.hyeeyoung.wishboard.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.model.WishItem
import com.hyeeyoung.wishboard.model.WishItemInfo
import com.hyeeyoung.wishboard.repository.home.WishRepository
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
        fetchItems()
    }

    private fun fetchItems() {
        Log.i(TAG, "fetchItems: $token")
        if (token == null) return
        viewModelScope.launch {
            val items = wishRepository.fetchItems(token)
            wishItems.postValue(items)
        }
    }

    fun fetchItemDetail(itemId: Int) {
        if (token == null) return
        viewModelScope.launch {
            val item = wishRepository.fetchItemDetail(token, itemId)
            wishItem.postValue(item)
        }
    }

    fun getWishItems(): LiveData<List<WishItem>?> = wishItems
    fun getWishItem(): LiveData<WishItemInfo?> = wishItem

    companion object {
        private val TAG = "WishViewModel"
    }
}