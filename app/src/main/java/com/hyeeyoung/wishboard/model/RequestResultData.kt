package com.hyeeyoung.wishboard.model

import com.google.gson.annotations.SerializedName

data class RequestResultData(
    // TODO need refactoring
    val success: Boolean,
    val message: String,
    val data: UserData,
) {
    data class UserData(
        val token: String,
        @SerializedName("push_state")
        val pushState: Int? = null,
    )
}