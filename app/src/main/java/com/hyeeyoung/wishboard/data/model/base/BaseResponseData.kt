package com.hyeeyoung.wishboard.data.model.base

import com.google.gson.annotations.SerializedName

data class BaseResponseData(
    // TODO need refactoring
    @SerializedName("result")
    val id: Long? = null,
    val token: String? = null,
    val pushState: Int? = null,
    val verificationCode: String
)