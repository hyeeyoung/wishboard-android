package com.hyeeyoung.wishboard.model

import com.google.gson.annotations.SerializedName

data class RequestResultId(
    val success: Boolean,
    val message: String,
    @SerializedName("data")
    val id: Long,
)