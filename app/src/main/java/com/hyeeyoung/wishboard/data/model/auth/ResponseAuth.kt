package com.hyeeyoung.wishboard.data.model.auth

data class ResponseAuth(
    val token: Token,
    val tempNickname: String?
)