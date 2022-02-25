package com.hyeeyoung.wishboard.remote

import com.hyeeyoung.wishboard.BuildConfig
import com.hyeeyoung.wishboard.model.RequestResult
import com.hyeeyoung.wishboard.model.RequestResultId
import com.hyeeyoung.wishboard.model.RequestResultToken
import com.hyeeyoung.wishboard.model.cart.CartItem
import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.model.noti.NotiItem
import com.hyeeyoung.wishboard.model.sign.SignInfo
import com.hyeeyoung.wishboard.model.sign.UserInfo
import com.hyeeyoung.wishboard.model.wish.WishItem
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface RemoteService {
    // 회원가입 및 로그인
    @POST("auth/signup")
    suspend fun signUpUser(
        @Body signInfo: SignInfo
    ): Response<RequestResultToken>

    @POST("auth/signin")
    suspend fun signInUser(
        @Body signInfo: SignInfo
    ): Response<RequestResultToken>

    // 위시리스트 및 아이템
    @GET("item")
    suspend fun fetchWishList(@Header("Authorization") token: String): Response<List<WishItem>>?

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

    @DELETE("item/{item_id}")
    suspend fun deleteWishItem(
        @Header("Authorization") token: String,
        @Path("item_id") itemId: Long
    ): Response<RequestResult>

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
    ): Response<RequestResultId>

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
    suspend fun fetchNotiList(
        @Header("Authorization") token: String,
    ): Response<List<NotiItem>>?

    @GET("noti/schedule")
    suspend fun updatePushNotiSettings(
        @Header("Authorization") token: String,
        @Query("push") isSet: Boolean,
    ): Response<RequestResult>

    @PUT("noti/{item_id}/read-state")
    suspend fun updateNotiReadState(
        @Header("Authorization") token: String,
        @Path("item_id") itemId: Long,
    ): Response<RequestResult>

    @GET("user")
    suspend fun fetchUserInfo(
        @Header("Authorization") token: String,
    ): Response<List<UserInfo>>?

    @FormUrlEncoded
    @PUT("user")
    suspend fun updateUserInfo(
        @Header("Authorization") token: String,
        @Field("nickname") nickname: String,
        @Field("profile_img") profileImage: String?,
    ): Response<RequestResult>

    // 사용자
    @FormUrlEncoded
    @PUT("user/fcm")
    suspend fun updateFCMToken(
        @Header("Authorization") userToken: String,
        @Field("fcm_token") fcmToken: String
    ): Response<RequestResult>

    companion object {
        val api = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RemoteService::class.java)
    }
}