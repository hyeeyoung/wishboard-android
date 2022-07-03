package com.hyeeyoung.wishboard.data.model.base

data class BaseResponseResult<T>(
    val success: Boolean,
    val message: String,
    val data: T,
)