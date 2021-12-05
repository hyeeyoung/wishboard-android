package com.hyeeyoung.wishboard.repository.sign

interface SignRepository {
    suspend fun signUp(email: String, password: String): Boolean
    suspend fun signIn(email: String, password: String): Boolean
}