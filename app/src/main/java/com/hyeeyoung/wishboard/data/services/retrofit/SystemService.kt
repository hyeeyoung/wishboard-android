package com.hyeeyoung.wishboard.data.services.retrofit

import com.hyeeyoung.wishboard.data.model.base.BaseResponseResult
import com.hyeeyoung.wishboard.data.model.system.AppVersionDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SystemService {
    @GET("version/check")
    suspend fun fetchAppVersion(
        @Query("osType") osType: String = "AOS"
    ): Response<BaseResponseResult<AppVersionDto>>
}