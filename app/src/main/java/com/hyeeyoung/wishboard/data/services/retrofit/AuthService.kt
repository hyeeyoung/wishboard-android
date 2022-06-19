package com.hyeeyoung.wishboard.data.services.retrofit

import com.hyeeyoung.wishboard.data.model.RequestResult
import com.hyeeyoung.wishboard.data.model.RequestResultData
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
}