package com.hyeeyoung.wishboard.domain.repositories

import com.hyeeyoung.wishboard.data.model.auth.ResponseSignIn

interface SignRepository {
    suspend fun signUp(email: String, password: String): Boolean
    suspend fun signIn(email: String, password: String): Result<ResponseSignIn?>
    suspend fun signInEmail(email: String): Boolean
    suspend fun requestVerificationMail(email: String): Pair<Pair<Boolean, String?>, Int>?
    suspend fun checkRegisteredUser(email: String): Pair<Boolean, Int>?
}