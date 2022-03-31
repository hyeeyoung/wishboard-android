package com.hyeeyoung.wishboard.repository.wish

import com.hyeeyoung.wishboard.model.wish.WishItem

interface WishRepository {
    suspend fun fetchWishList(token: String): List<WishItem>?
    suspend fun fetchLatestWishItem(token: String): WishItem?
    suspend fun uploadWishItem(token: String, wishItem: WishItem): Boolean
    suspend fun updateWishItem(token: String, itemId: Long, wishItem: WishItem): Boolean
    suspend fun deleteWishItem(token: String, itemId: Long): Boolean
}