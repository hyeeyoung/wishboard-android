package com.hyeeyoung.wishboard.domain.repositories

import com.hyeeyoung.wishboard.data.model.user.UserInfo
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface UserRepository {
    suspend fun fetchUserInfo(): UserInfo?
    suspend fun updateUserInfo(
        nickname: RequestBody?,
        profileImg: MultipartBody.Part?
    ): Pair<Boolean, Int>?

    suspend fun registerFCMToken(fcmToken: String?): Boolean
    suspend fun deleteUserAccount(): Result<Boolean?>
    suspend fun changePassword(password: String): Result<Boolean>
}