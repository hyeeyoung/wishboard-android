package com.hyeeyoung.wishboard.model.wish

import com.google.gson.annotations.SerializedName

data class ItemInfo(
    @SerializedName("item_img")
    val image: String? = null,
    @SerializedName("item_name")
    val name: String? = null,
    @SerializedName("item_price")
    val price: String? = null
)
