package com.hyeeyoung.wishboard.data.services.retrofit

import com.hyeeyoung.wishboard.data.model.base.BaseResponseData
import com.hyeeyoung.wishboard.data.model.base.BaseResponseResult
import com.hyeeyoung.wishboard.data.model.folder.FolderItem
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import retrofit2.Response
import retrofit2.http.*

interface FolderService {
    @GET("folder")
    suspend fun fetchFolderList(): Response<List<FolderItem>>?

    @GET("folder/list")
    suspend fun fetchFolderListSummary(): Response<List<FolderItem>>?

    @GET("folder/item/{folder_id}")
    suspend fun fetchItemsInFolder(@Path("folder_id") folderId: Long): Response<List<WishItem>>?

    @POST("folder")
    suspend fun createNewFolder(@Body folderItem: FolderItem): Response<BaseResponseResult<BaseResponseData>>

    @FormUrlEncoded
    @PUT("folder/{folder_id}")
    suspend fun updateFolderName(
        @Path("folder_id") folderId: Long,
        @Field("folder_name") folderName: String
    ): Response<BaseResponseResult<BaseResponseData?>>

    @DELETE("folder/{folder_id}")
    suspend fun deleteFolder(@Path("folder_id") folderId: Long): Response<BaseResponseResult<BaseResponseData?>>
}