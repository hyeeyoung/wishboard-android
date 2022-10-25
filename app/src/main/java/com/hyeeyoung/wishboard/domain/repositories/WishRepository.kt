package com.hyeeyoung.wishboard.domain.repositories

import com.hyeeyoung.wishboard.data.model.wish.ItemInfo
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface WishRepository {
    suspend fun fetchWishList(token: String): List<WishItem>?
    suspend fun fetchLatestWishItem(token: String): WishItem?
    suspend fun uploadWishItem(
        token: String, folderId: RequestBody?,
        itemName: RequestBody,
        itemPrice: RequestBody?,
        itemMemo: RequestBody?,
        itemUrl: RequestBody?,
        itemNotificationType: RequestBody?,
        itemNotificationDate: RequestBody?,
        image: MultipartBody.Part
    ): Boolean
    suspend fun updateWishItem(token: String, itemId: Long, wishItem: WishItem): Pair<Boolean, Int>?
    suspend fun updateFolderOfWishItem(token: String, folderId: Long, itemId: Long): Boolean
    suspend fun deleteWishItem(token: String, itemId: Long): Boolean
    suspend fun getItemParsingInfo(site: String): Pair<ItemInfo?, Int>?
}