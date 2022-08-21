package com.hyeeyoung.wishboard.domain.repositories

import com.hyeeyoung.wishboard.data.model.wish.ItemInfo
import com.hyeeyoung.wishboard.data.model.wish.WishItem

interface WishRepository {
    suspend fun fetchWishList(token: String): List<WishItem>?
    suspend fun fetchLatestWishItem(token: String): WishItem?
    suspend fun uploadWishItem(token: String, wishItem: WishItem): Boolean
    suspend fun updateWishItem(token: String, itemId: Long, wishItem: WishItem): Pair<Boolean, Int>?
    suspend fun updateFolderOfWishItem(token: String, folderId: Long, itemId: Long): Boolean
    suspend fun deleteWishItem(token: String, itemId: Long): Boolean
    suspend fun getItemParsingInfo(site: String): ItemInfo?
}