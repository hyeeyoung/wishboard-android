package com.hyeeyoung.wishboard.repository.sign

interface SignRepository {
    suspend fun signUp(email: String, password: String): Boolean
    suspend fun signIn(email: String, password: String): Boolean
    suspend fun signInEmail(email: String): Boolean
    suspend fun requestVerificationMail(email: String): Pair<Pair<Boolean, String?>, Int>
}