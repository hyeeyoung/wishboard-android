package com.hyeeyoung.wishboard.model

data class RequestResultData(
    // TODO need refactoring
    val success: Boolean,
    val message: String,
    val data: ResultData,
) {
    data class ResultData(
        val result: Long? = null,
        val token: String? = null,
        val pushState: Int? = null,
    )
}