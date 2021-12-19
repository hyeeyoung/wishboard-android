package com.hyeeyoung.wishboard.remote

import com.hyeeyoung.wishboard.model.WishItem
import com.hyeeyoung.wishboard.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*

interface RemoteService {
    @POST("auth/signup")
    suspend fun signUpUser(
        @Body signInfo: SignInfo
    ): Response<RequestResult>

    @POST("auth/signin")
    suspend fun signInUser(
        @Body signInfo: SignInfo
    ): Response<RequestResult>

    ): Response<ResponseBody>

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