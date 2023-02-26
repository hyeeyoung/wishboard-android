package com.hyeeyoung.wishboard.data.model.auth

data class ResponseSignIn(
    val token: Token,
    val nickname: String?
)