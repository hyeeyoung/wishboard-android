package com.hyeeyoung.wishboard.repository.wish

import com.hyeeyoung.wishboard.model.wish.WishItem

interface WishRepository {
    suspend fun fetchWishList(token: String): List<WishItem>?
    suspend fun uploadWishItem(token: String, wishItem: WishItem): Boolean
}