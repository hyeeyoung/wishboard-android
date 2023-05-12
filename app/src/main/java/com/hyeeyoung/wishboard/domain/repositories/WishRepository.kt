package com.hyeeyoung.wishboard.domain.repositories

import com.hyeeyoung.wishboard.data.model.wish.ItemDetail
import com.hyeeyoung.wishboard.data.model.wish.ItemInfo
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface WishRepository {
    suspend fun fetchWishList(): List<WishItem>?
    suspend fun fetchLatestWishItem(): WishItem?
    suspend fun fetchWishItemDetail(itemId: Long): List<ItemDetail>?
    suspend fun uploadWishItem(
        folderId: RequestBody?,
        itemName: RequestBody,
        itemPrice: RequestBody?,
        itemUrl: RequestBody?,
        itemNotificationType: RequestBody?,
        itemNotificationDate: RequestBody?,
        image: MultipartBody.Part?,
        itemMemo: RequestBody? = null,
    ): Boolean

    suspend fun updateWishItem(
        itemId: Long,
        folderId: RequestBody?,
        itemName: RequestBody,
        itemPrice: RequestBody?,
        itemMemo: RequestBody?,
        itemUrl: RequestBody?,
        itemNotificationType: RequestBody?,
        itemNotificationDate: RequestBody?,
        itemImage: MultipartBody.Part?
    ): Pair<Boolean, Int>?

    suspend fun updateFolderOfWishItem(itemId: Long, folderId: Long): Boolean
    suspend fun deleteWishItem(itemId: Long): Boolean
    suspend fun getItemParsingInfo(site: String): Pair<ItemInfo?, Int>?
}