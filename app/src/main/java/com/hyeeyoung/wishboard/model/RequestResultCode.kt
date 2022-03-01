package com.hyeeyoung.wishboard.model

import com.google.gson.annotations.SerializedName

data class RequestResultCode(
    // TODO need refactoring
    val success: Boolean,
    val message: String,
    val data: ResultData,
) {
    data class ResultData(
        @SerializedName("randomNumber")
        val verificationCode: String
    )
}