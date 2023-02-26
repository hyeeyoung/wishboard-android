package com.hyeeyoung.wishboard.data.services.retrofit

import com.hyeeyoung.wishboard.data.model.base.BaseResponseData
import com.hyeeyoung.wishboard.data.model.base.BaseResponseResult
import com.hyeeyoung.wishboard.data.model.wish.ItemDetail
import com.hyeeyoung.wishboard.data.model.wish.ItemInfo
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface WishItemService {
    @GET("item")
    suspend fun fetchWishList(): Response<List<WishItem>?>

    @GET("item/latest")
    suspend fun fetchLatestWishItem(): Response<List<WishItem>>?

    @GET("item/{item_id}")
    suspend fun fetchWishItemDetail(
        @Path("item_id") itemId: Long
    ): Response<List<ItemDetail>>

    @Multipart
    @POST("/item")
    suspend fun uploadWishItem(
        @Part("folder_id") folderId: RequestBody?,
        @Part("item_name") itemName: RequestBody,
        @Part("item_price") itemPrice: RequestBody?,
        @Part("item_memo") itemMemo: RequestBody?,
        @Part("item_url") itemUrl: RequestBody?,
        @Part("item_notification_type") itemNotificationType: RequestBody?,
        @Part("item_notification_date") itemNotificationDate: RequestBody?,
        @Part itemImg: MultipartBody.Part?,
    ): Response<BaseResponseResult<BaseResponseData?>>

    @Multipart
    @PUT("item/{item_id}")
    suspend fun updateToWishItem(
        @Path("item_id") itemId: Long,
        @Part("folder_id") folderId: RequestBody?,
        @Part("item_name") itemName: RequestBody,
        @Part("item_price") itemPrice: RequestBody?,
        @Part("item_memo") itemMemo: RequestBody?,
        @Part("item_url") itemUrl: RequestBody?,
        @Part("item_notification_type") itemNotificationType: RequestBody?,
        @Part("item_notification_date") itemNotificationDate: RequestBody?,
        @Part itemImg: MultipartBody.Part?,
    ): Response<BaseResponseResult<BaseResponseData?>>

    @PUT("item/{item_id}/folder/{folder_id}")
    suspend fun updateFolderOfItem(
        @Path("item_id") itemId: Long,
        @Path("folder_id") folderId: Long
    ): Response<BaseResponseResult<BaseResponseData?>>

    @DELETE("item/{item_id}")
    suspend fun deleteWishItem(@Path("item_id") itemId: Long): Response<BaseResponseResult<BaseResponseData?>>

    @GET("item/parse?site=")
    suspend fun getItemParsingInfo(
        @Query("site") site: String
    ): Response<BaseResponseResult<ItemInfo>?>
}