package com.hyeeyoung.wishboard.repository.wish

import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.model.wish.WishItemInfo

interface WishRepository {
    suspend fun fetchWishList(token: String): List<WishItem>?
    suspend fun fetchWishItem(itemId: Int): WishItemInfo?
}