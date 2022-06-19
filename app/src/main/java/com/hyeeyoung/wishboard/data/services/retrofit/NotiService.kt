package com.hyeeyoung.wishboard.data.services.retrofit

import com.hyeeyoung.wishboard.data.model.base.BaseResponseData
import com.hyeeyoung.wishboard.data.model.base.BaseResponseResult
import com.hyeeyoung.wishboard.data.model.noti.NotiItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotiService {
    @GET("noti")
    suspend fun fetchPreviousNotiList(
        @Header("Authorization") token: String,
    ): Response<List<NotiItem>>?

    @GET("noti/calendar")
    suspend fun fetchAllNotiList(
        @Header("Authorization") token: String,
    ): Response<List<NotiItem>>?

    @PUT("noti/{item_id}/read-state")
    suspend fun updateNotiReadState(
        @Header("Authorization") token: String,
        @Path("item_id") itemId: Long,
    ): Response<BaseResponseResult<BaseResponseData?>>
}