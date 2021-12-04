package com.hyeeyoung.wishboard.remote

import com.hyeeyoung.wishboard.model.SignInfo
import com.hyeeyoung.wishboard.model.UserInfo
import com.hyeeyoung.wishboard.model.WishItem
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*

interface RemoteService {
    @POST("user/signup")
    suspend fun signUpUser(
        @Body signInfo: SignInfo
    ): Response<ResponseBody>

    @POST("user/signup")
    suspend fun signInUser(@Body userItem: UserInfo): Response<UserInfo>?

    companion object {
        private const val BASE_URL = "http://13.124.4.46/"
        private const val IMAGE_URL =
            "https://wishboardbucket.s3.ap-northeast-2.amazonaws.com/wishboard/"

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RemoteService::class.java)
    }
}