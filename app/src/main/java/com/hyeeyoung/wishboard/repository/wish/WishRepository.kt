package com.hyeeyoung.wishboard.repository.wish

import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.model.wish.WishItemInfo
import com.hyeeyoung.wishboard.model.wish.WishItemRegistrationInfo

interface WishRepository {
    suspend fun fetchWishList(token: String): List<WishItem>?
    suspend fun fetchWishItem(itemId: Long): WishItemInfo?
    suspend fun uploadWishItem(token: String, wishItem: WishItemRegistrationInfo): Boolean
}