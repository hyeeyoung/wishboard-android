package com.hyeeyoung.wishboard.model.sign

import com.google.gson.annotations.SerializedName

data class UserInfo(
    var email: String,
    var nickname: String?,
    @SerializedName("profile_img")
    var profileImage: String?,
    @SerializedName("option_notification")
    val notiOption: Boolean,
    @SerializedName("fcm_token")
    var token: String?
)
