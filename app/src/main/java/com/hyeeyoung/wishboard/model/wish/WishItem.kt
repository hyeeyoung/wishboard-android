package com.hyeeyoung.wishboard.model.wish

import com.google.gson.annotations.SerializedName

data class WishItem (
    @SerializedName("item_id")
    val itemId: Long,
    @SerializedName("item_img")
    val image: String,
    @SerializedName("item_name")
    val name: String,
    @SerializedName("item_price")
    val price: Int?,
    @SerializedName("cart_item_id")
    val cartId: Long?,
)
