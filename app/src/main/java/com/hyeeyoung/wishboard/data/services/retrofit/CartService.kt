package com.hyeeyoung.wishboard.data.services.retrofit

import com.hyeeyoung.wishboard.data.model.base.BaseResponseData
import com.hyeeyoung.wishboard.data.model.base.BaseResponseResult
import com.hyeeyoung.wishboard.data.model.cart.CartItem
import retrofit2.Response
import retrofit2.http.*

interface CartService {
    @GET("cart")
    suspend fun fetchCart(): Response<List<CartItem>>?

    @FormUrlEncoded
    @POST("cart")
    suspend fun addToCart(
        @Field("item_id") itemId: Long
    ): Response<BaseResponseResult<BaseResponseData?>>

    @DELETE("cart/{item_id}")
    suspend fun removeToCart(
        @Path("item_id") itemId: Long
    ): Response<BaseResponseResult<BaseResponseData?>>

    @FormUrlEncoded
    @PUT("cart/{item_id}")
    suspend fun updateToCart(
        @Path("item_id") itemId: Long,
        @Field("item_count") itemCount: Int
    ): Response<BaseResponseResult<BaseResponseData?>>
}