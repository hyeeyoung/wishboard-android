package com.hyeeyoung.wishboard.model.cart

import com.google.gson.annotations.SerializedName

data class CartItem (
    @SerializedName("item_id")
    val itemId: Long,
    @SerializedName("item_img")
    val image: String,
    @SerializedName("item_name")
    val name: String,
    @SerializedName("item_price")
    val price: Int?,
    @SerializedName("item_count")
    val count: Int,
)
