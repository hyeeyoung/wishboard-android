package com.hyeeyoung.wishboard.data.services.retrofit

import com.hyeeyoung.wishboard.data.model.base.BaseResponseData
import com.hyeeyoung.wishboard.data.model.base.BaseResponseResult
import com.hyeeyoung.wishboard.data.model.user.UserInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthService {
    @POST("auth/signup")
    suspend fun signUpUser(
        @Body userInfo: UserInfo
    ): Response<BaseResponseResult<BaseResponseData>>

    @POST("auth/signin")
    suspend fun signInUser(
        @Body userInfo: UserInfo
    ): Response<BaseResponseResult<BaseResponseData>>

    @FormUrlEncoded
    @POST("auth/password-mail")
    suspend fun requestVerificationMail(
        @Field("email") email: String
    ): Response<BaseResponseResult<BaseResponseData>>

    @FormUrlEncoded
    @POST("auth/re-signin")
    suspend fun signInEmail(
        @Field("verify") isVerify: Boolean,
        @Field("email") email: String
    ): Response<BaseResponseResult<BaseResponseData>>

    @FormUrlEncoded
    @POST("auth/check-email")
    suspend fun checkRegisteredUser(
        @Field("email") email: String
    ): Response<BaseResponseResult<BaseResponseData?>>
}