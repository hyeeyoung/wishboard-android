package com.hyeeyoung.wishboard.model.wish

import com.google.gson.annotations.SerializedName

data class WishItemRegistrationInfo ( // TODO WishItemInfo, WishItemRegistrationInfo -> WishItem으로 합치기, 서버 코드 수정 필요
    @SerializedName("item_name")
    val name: String,
    @SerializedName("item_img")
    val image: String,
    @SerializedName("item_price")
    val price: String,
    @SerializedName("item_url")
    val url: String,
    @SerializedName("item_memo")
    val memo: String?,
)
