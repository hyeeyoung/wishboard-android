package com.hyeeyoung.wishboard.model.sign

import com.google.gson.annotations.SerializedName

data class UserInfo( // TODO need refactoring
    var email: String? = null,
    var nickname: String? = null,
    @SerializedName("profile_img")
    var profileImage: String? = null,
    @SerializedName("option_notification")
    val notiOption: Boolean = false,
    @SerializedName("fcm_token")
    var token: String? = null
)
