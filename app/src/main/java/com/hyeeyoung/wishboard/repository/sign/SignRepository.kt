package com.hyeeyoung.wishboard.repository.sign

interface SignRepository {
    suspend fun signUp(email: String, password: String): Boolean
}