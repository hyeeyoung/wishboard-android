package com.hyeeyoung.wishboard.repository.user

interface UserRepository {
    suspend fun registerFCMToken(userToken: String, fcmToken: String): Boolean
    suspend fun updateUserNickname(userToken: String, fcmToken: String): Boolean
}