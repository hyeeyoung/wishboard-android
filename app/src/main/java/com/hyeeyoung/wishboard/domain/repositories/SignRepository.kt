package com.hyeeyoung.wishboard.domain.repositories

import com.hyeeyoung.wishboard.data.model.auth.ResponseAuth

interface SignRepository {
    suspend fun signUp(email: String, password: String, fcmToken: String): Result<ResponseAuth?>
    suspend fun signIn(email: String, password: String, fcmToken: String): Result<ResponseAuth?>
    suspend fun signInEmail(email: String, fcmToken: String): Result<ResponseAuth?>
    suspend fun requestVerificationMail(email: String): Pair<Pair<Boolean, String?>, Int>?
    suspend fun checkRegisteredUser(email: String): Pair<Boolean, Int>?
    suspend fun logout(): Result<Boolean?>
}