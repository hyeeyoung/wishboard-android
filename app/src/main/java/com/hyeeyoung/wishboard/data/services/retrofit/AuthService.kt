package com.hyeeyoung.wishboard.data.services.retrofit

import com.hyeeyoung.wishboard.data.model.auth.RequestAuth
import com.hyeeyoung.wishboard.data.model.auth.ResponseAuth
import com.hyeeyoung.wishboard.data.model.auth.ResponseVerificationMail
import com.hyeeyoung.wishboard.data.model.base.BaseResponse
import com.hyeeyoung.wishboard.data.model.base.BaseResponseResult
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthService {
    @POST("auth/signup")
    suspend fun signUpUser(
        @Body requestAuth: RequestAuth
    ): Response<BaseResponseResult<ResponseAuth?>>

    @POST("auth/signin")
    suspend fun signInUser(
        @Body requestAuth: RequestAuth
    ): Response<BaseResponseResult<ResponseAuth?>>

    @FormUrlEncoded
    @POST("auth/password-mail")
    suspend fun requestVerificationMail(
        @Field("email") email: String
    ): Response<BaseResponseResult<ResponseVerificationMail?>>

    @FormUrlEncoded
    @POST("auth/re-signin")
    suspend fun signInEmail(
        @Field("verify") isVerify: Boolean,
        @Field("email") email: String,
        @Field("fcmToken") fcmToken: String
    ): Response<BaseResponseResult<ResponseAuth?>>

    @FormUrlEncoded
    @POST("auth/check-email")
    suspend fun checkRegisteredUser(
        @Field("email") email: String
    ): Response<BaseResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<BaseResponse>
}