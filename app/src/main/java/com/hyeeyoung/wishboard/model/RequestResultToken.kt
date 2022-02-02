package com.hyeeyoung.wishboard.model

import com.google.gson.annotations.SerializedName

data class RequestResultToken(
    val success: Boolean,
    val message: String,
    @SerializedName("data")
    val token: String,
)