package com.hyeeyoung.wishboard.model

import com.google.gson.annotations.SerializedName

data class RequestResultData(
    // TODO need refactoring
    val success: Boolean,
    val message: String,
    val data: ResultData,
) {
    data class ResultData(
        @SerializedName("result")
        val id: Long? = null,
        val token: String? = null,
        val pushState: Int? = null,
    )
}