package com.hyeeyoung.wishboard.remote

import com.hyeeyoung.wishboard.model.RequestResult
import com.hyeeyoung.wishboard.model.RequestResultToken
import com.hyeeyoung.wishboard.model.cart.CartItem
import com.hyeeyoung.wishboard.model.sign.SignInfo
import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.model.wish.WishItemRegistrationInfo
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

    @GET("cart")
    suspend fun fetchCart(@Header("Authorization") token: String): Response<List<CartItem>>?

    @POST("item")
    suspend fun uploadWishItem(@Header("Authorization") token: String, @Body wishItem: WishItemRegistrationInfo): Response<RequestResult>

    // 장바구니
    @POST("cart")
    suspend fun addToCart(
        @Header("Authorization") token: String,
        @Body item_id: Long
    ): Response<RequestResult>

    @HTTP(method = "DELETE", path = "cart", hasBody = true)
    suspend fun removeToCart(
        @Header("Authorization") token: String,
        @Body item_id: Long
    ): Response<RequestResult>

    companion object {
        // TODO BASE_URL 숨기기
        private const val BASE_URL = "http://ec2-3-35-13-182.ap-northeast-2.compute.amazonaws.com"
        private const val IMAGE_URL = "https://wishboardbucket.s3.ap-northeast-2.amazonaws.com/wishboard/"

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RemoteService::class.java)
    }
}