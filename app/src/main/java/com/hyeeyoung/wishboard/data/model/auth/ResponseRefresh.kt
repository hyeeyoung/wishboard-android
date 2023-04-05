package com.hyeeyoung.wishboard.data.model.auth

data class ResponseRefresh(
    val success: Boolean,
    val message: String,
    val data: ResponseToken?,
) {
    data class ResponseToken(
        val token: Token,
    )
}
