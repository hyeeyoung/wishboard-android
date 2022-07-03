package com.hyeeyoung.wishboard.domain.repositories

interface SignRepository {
    suspend fun signUp(email: String, password: String): Boolean
    suspend fun signIn(email: String, password: String): Boolean
    suspend fun signInEmail(email: String): Boolean
    suspend fun requestVerificationMail(email: String): Pair<Pair<Boolean, String?>, Int>?
    suspend fun checkRegisteredUser(email: String): Pair<Boolean, Int>?
}