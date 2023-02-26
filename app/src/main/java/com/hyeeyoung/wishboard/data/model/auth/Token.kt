package com.hyeeyoung.wishboard.data.model.auth

data class Token(
    val accessToken: String,
    val refreshToken: String,
)