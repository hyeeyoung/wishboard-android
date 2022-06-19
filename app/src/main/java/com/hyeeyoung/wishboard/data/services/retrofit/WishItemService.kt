package com.hyeeyoung.wishboard.data.services.retrofit

import com.hyeeyoung.wishboard.data.model.BaseRequestResult
import com.hyeeyoung.wishboard.data.model.RequestResult
import com.hyeeyoung.wishboard.data.model.wish.ItemInfo
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import retrofit2.Response
import retrofit2.http.*

interface WishItemService {
    @GET("item")
    suspend fun fetchWishList(@Header("Authorization") token: String): Response<List<WishItem>>?

    @GET("item/latest")
    suspend fun fetchLatestWishItem(@Header("Authorization") token: String): Response<List<WishItem>>?

    @POST("item")
    suspend fun uploadWishItem(
        @Header("Authorization") token: String,
        @Body wishItem: WishItem
    ): Response<RequestResult>

    @PUT("item/{item_id}")
    suspend fun updateToWishItem(
        @Header("Authorization") token: String,
        @Path("item_id") itemId: Long,
        @Body wishItem: WishItem
    ): Response<RequestResult>

    @PUT("item/{item_id}/folder/{folder_id}")
    suspend fun updateFolderOfItem(
        @Header("Authorization") token: String,
        @Path("item_id") itemId: Long,
        @Path("folder_id") folderId: Long
    ): Response<RequestResult>

    @DELETE("item/{item_id}")
    suspend fun deleteWishItem(
        @Header("Authorization") token: String,
        @Path("item_id") itemId: Long
    ): Response<RequestResult>

    @GET("item/parse?site=")
    suspend fun getItemParsingInfo(
        @Query("site") site: String
    ): Response<BaseRequestResult<ItemInfo>?>
}