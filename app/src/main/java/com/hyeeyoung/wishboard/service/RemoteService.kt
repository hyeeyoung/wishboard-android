package com.hyeeyoung.wishboard.service

import com.hyeeyoung.wishboard.BuildConfig
import com.hyeeyoung.wishboard.model.BaseRequestResult
import com.hyeeyoung.wishboard.model.RequestResult
import com.hyeeyoung.wishboard.model.RequestResultData
import com.hyeeyoung.wishboard.model.cart.CartItem
import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.model.noti.NotiItem
import com.hyeeyoung.wishboard.model.user.UserInfo
import com.hyeeyoung.wishboard.model.wish.ItemInfo
import com.hyeeyoung.wishboard.model.wish.WishItem
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface RemoteService {
    // 회원가입 및 로그인
    @POST("auth/signup")
    suspend fun signUpUser(
        @Body userInfo: UserInfo
    ): Response<RequestResultData>

    @POST("auth/signin")
    suspend fun signInUser(
        @Body userInfo: UserInfo
    ): Response<RequestResultData>

    @FormUrlEncoded
    @POST("auth/password-mail")
    suspend fun requestVerificationMail(
        @Field("email") email: String
    ): Response<RequestResultData>

    @FormUrlEncoded
    @POST("auth/re-signin")
    suspend fun signInEmail(
        @Field("verify") isVerify: Boolean,
        @Field("email") email: String
    ): Response<RequestResultData>


    @FormUrlEncoded
    @POST("auth/check-email")
    suspend fun checkRegisteredUser(
        @Field("email") email: String
    ): Response<RequestResult>

    // 위시리스트 및 아이템
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

    // 장바구니
    @GET("cart")
    suspend fun fetchCart(@Header("Authorization") token: String): Response<List<CartItem>>?

    @FormUrlEncoded
    @POST("cart")
    suspend fun addToCart(
        @Header("Authorization") token: String,
        @Field("item_id") itemId: Long
    ): Response<RequestResult>

    @DELETE("cart/{item_id}")
    suspend fun removeToCart(
        @Header("Authorization") token: String,
        @Path("item_id") itemId: Long
    ): Response<RequestResult>

    @FormUrlEncoded
    @PUT("cart/{item_id}")
    suspend fun updateToCart(
        @Header("Authorization") token: String,
        @Path("item_id") itemId: Long,
        @Field("item_count") itemCount: Int
    ): Response<RequestResult>

    // 폴더
    @GET("folder")
    suspend fun fetchFolderList(
        @Header("Authorization") token: String
    ): Response<List<FolderItem>>?

    @GET("folder/list")
    suspend fun fetchFolderListSummary(
        @Header("Authorization") token: String
    ): Response<List<FolderItem>>?

    @GET("folder/item/{folder_id}")
    suspend fun fetchItemsInFolder(
        @Header("Authorization") token: String,
        @Path("folder_id") folderId: Long
    ): Response<List<WishItem>>?

    @POST("folder")
    suspend fun createNewFolder(
        @Header("Authorization") token: String,
        @Body folderItem: FolderItem
    ): Response<RequestResultData>

    @FormUrlEncoded
    @PUT("folder/{folder_id}")
    suspend fun updateFolderName(
        @Header("Authorization") token: String,
        @Path("folder_id") folderId: Long,
        @Field("folder_name") folderName: String
    ): Response<RequestResult>

    @DELETE("folder/{folder_id}")
    suspend fun deleteFolder(
        @Header("Authorization") token: String,
        @Path("folder_id") folderId: Long,
    ): Response<RequestResult>

    // 알림
    @GET("noti")
    suspend fun fetchPreviousNotiList(
        @Header("Authorization") token: String,
    ): Response<List<NotiItem>>?

    @GET("noti/calendar")
    suspend fun fetchAllNotiList(
        @Header("Authorization") token: String,
    ): Response<List<NotiItem>>?

    @PUT("noti/{item_id}/read-state")
    suspend fun updateNotiReadState(
        @Header("Authorization") token: String,
        @Path("item_id") itemId: Long,
    ): Response<RequestResult>

    @GET("user")
    suspend fun fetchUserInfo(
        @Header("Authorization") token: String,
    ): Response<List<UserInfo>>?

    @PUT("user")
    suspend fun updateUserInfo(
        @Header("Authorization") token: String,
        @Body userInfo: UserInfo,
    ): Response<RequestResult>

    // 사용자
    @FormUrlEncoded
    @PUT("user/fcm")
    suspend fun updateFCMToken(
        @Header("Authorization") userToken: String,
        @Field("fcm_token") fcmToken: String?
    ): Response<RequestResult>

    @PUT("user/active")
    suspend fun deleteUserAccount(
        @Header("Authorization") token: String,
    ): Response<RequestResult>

    @PUT("user/push-state/{push}")
    suspend fun updatePushState(
        @Header("Authorization") token: String,
        @Path("push") push: Boolean,
    ): Response<RequestResult>

    companion object {
        val api = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RemoteService::class.java)
    }
}