package com.hyeeyoung.wishboard.data.model.auth

data class RequestAuth(
    val email: String,
    val password: String,
    val fcmToken: String,
)