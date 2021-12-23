package com.hyeeyoung.wishboard.repository.home

import com.hyeeyoung.wishboard.model.WishItem
import com.hyeeyoung.wishboard.model.WishItemInfo

interface WishRepository {
    suspend fun fetchItems(token: String): List<WishItem>?
    suspend fun fetchItemDetail(token: String, itemId: Int): WishItemInfo?
}