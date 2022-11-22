package com.hyeeyoung.wishboard.domain.repositories

import com.hyeeyoung.wishboard.data.model.user.UserInfo
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface UserRepository {
    suspend fun fetchUserInfo(userToken: String): UserInfo?
    suspend fun updateUserInfo(userToken: String, nickname: RequestBody?, profileImg: MultipartBody.Part?): Pair<Boolean, Int>?
    suspend fun registerFCMToken(userToken: String, fcmToken: String?): Boolean
    suspend fun deleteUserAccount(userToken: String): Boolean
}