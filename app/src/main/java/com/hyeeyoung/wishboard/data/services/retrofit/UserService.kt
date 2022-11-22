package com.hyeeyoung.wishboard.data.services.retrofit

import com.hyeeyoung.wishboard.data.model.base.BaseResponse
import com.hyeeyoung.wishboard.data.model.base.BaseResponseData
import com.hyeeyoung.wishboard.data.model.base.BaseResponseResult
import com.hyeeyoung.wishboard.data.model.user.UserInfo
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface UserService {
    @GET("user")
    suspend fun fetchUserInfo(
        @Header("Authorization") token: String,
    ): Response<List<UserInfo>>?

    @Multipart
    @PUT("user")
    suspend fun updateUserInfo(
        @Header("Authorization") token: String,
        @Part("nickname") nickname: RequestBody?,
        @Part profileImg: MultipartBody.Part?,
    ): Response<BaseResponseResult<BaseResponseData?>>

    @FormUrlEncoded
    @PUT("user/fcm")
    suspend fun updateFCMToken(
        @Header("Authorization") userToken: String,
        @Field("fcm_token") fcmToken: String?
    ): Response<BaseResponseResult<BaseResponseData?>>

    @PUT("user/active")
    suspend fun deleteUserAccount(
        @Header("Authorization") token: String,
    ): Response<BaseResponse>

    @PUT("user/push-state/{push}")
    suspend fun updatePushState(
        @Header("Authorization") token: String,
        @Path("push") push: Boolean,
    ): Response<BaseResponseResult<BaseResponseData?>>
}