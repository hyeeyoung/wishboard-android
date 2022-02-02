package com.hyeeyoung.wishboard.model

data class RequestResultToken(
    val success: Boolean,
    val message: String,
    val data: String,
)