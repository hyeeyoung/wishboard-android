package com.hyeeyoung.wishboard.model

data class BaseRequestResult<T>(
    // TODO need refactoring
    val success: Boolean,
    val message: String,
    val data: T,
)