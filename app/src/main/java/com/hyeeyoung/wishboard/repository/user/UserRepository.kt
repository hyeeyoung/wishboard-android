package com.hyeeyoung.wishboard.repository.user

import com.hyeeyoung.wishboard.model.user.UserInfo

interface UserRepository {
    suspend fun fetchUserInfo(userToken: String): UserInfo?
    suspend fun updateUserInfo(userToken: String, nickname: String?, imageFileName: String?): Pair<Boolean, Int>
    suspend fun registerFCMToken(userToken: String, fcmToken: String?): Boolean
}