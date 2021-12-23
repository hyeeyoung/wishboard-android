package com.hyeeyoung.wishboard.model

import com.google.gson.annotations.SerializedName

data class CartItem (
    @SerializedName("item_id")
    val itemId: Int,
    @SerializedName("item_img")
    val image: String,
    @SerializedName("item_name")
    val name: String,
    @SerializedName("item_price")
    val price: String,
    @SerializedName("item_count")
    val count: String,
)
