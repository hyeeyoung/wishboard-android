package com.hyeeyoung.wishboard.remote

import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.model.*
import com.hyeeyoung.wishboard.model.cart.CartItem
import com.hyeeyoung.wishboard.model.sign.SignInfo
import com.hyeeyoung.wishboard.model.wish.WishItemInfo
import com.hyeeyoung.wishboard.model.wish.WishItemRegistrationInfo
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*

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
    @GET("item/home")
    suspend fun fetchWishList(@Header("Authorization") token: String): Response<List<WishItem>>?

    @GET("item/detail/{item_id}")
    suspend fun fetchWishItem(@Path("item_id") itemId: Int): Response<List<WishItemInfo>>?

    @GET("cart")
    suspend fun fetchCart(@Header("Authorization") token: String): Response<List<CartItem>>?

    @POST("item")
    suspend fun uploadWishItem(@Header("Authorization") token: String, @Body wishItem: WishItemRegistrationInfo): Response<RequestResult>

    // 장바구니
    @POST("cart")
    suspend fun addToCart(
        @Header("Authorization") token: String,
        @Body item_id: Int
    ): Response<RequestResult>

    @HTTP(method = "DELETE", path = "cart", hasBody = true)
    suspend fun removeToCart(
        @Header("Authorization") token: String,
        @Body item_id: Int
    ): Response<RequestResult>

    companion object {
        private const val BASE_URL = "http://ec2-54-180-126-188.ap-northeast-2.compute.amazonaws.com"
        private const val IMAGE_URL = "https://wishboardbucket.s3.ap-northeast-2.amazonaws.com/wishboard/"

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RemoteService::class.java)
    }
}