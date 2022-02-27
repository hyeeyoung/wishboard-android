package com.hyeeyoung.wishboard.model.sign

import com.google.gson.annotations.SerializedName

data class UserInfo( // TODO need refactoring
    var email: String? = null,
    var password: String? = null,
    var nickname: String? = null,
    @SerializedName("profile_img")
    var profileImage: String? = null,
    @SerializedName("push_state")
    val pushState: Int? = null,
    @SerializedName("fcm_token")
    var token: String? = null
)
