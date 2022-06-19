package com.hyeeyoung.wishboard.domain.repositories

import com.hyeeyoung.wishboard.data.model.user.UserInfo

interface UserRepository {
    suspend fun fetchUserInfo(userToken: String): UserInfo?
    suspend fun updateUserInfo(userToken: String, nickname: String?, imageFileName: String?): Pair<Boolean, Int>?
    suspend fun registerFCMToken(userToken: String, fcmToken: String?): Boolean
    suspend fun deleteUserAccount(userToken: String): Boolean
}