package com.hyeeyoung.wishboard.data.services.retrofit

import com.hyeeyoung.wishboard.data.model.RequestResult
import com.hyeeyoung.wishboard.data.model.user.UserInfo
import retrofit2.Response
import retrofit2.http.*

interface UserService {
    @GET("user")
    suspend fun fetchUserInfo(
        @Header("Authorization") token: String,
    ): Response<List<UserInfo>>?

    @PUT("user")
    suspend fun updateUserInfo(
        @Header("Authorization") token: String,
        @Body userInfo: UserInfo,
    ): Response<RequestResult>

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
}