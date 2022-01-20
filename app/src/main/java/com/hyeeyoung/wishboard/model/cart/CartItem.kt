package com.hyeeyoung.wishboard.model.cart

import com.google.gson.annotations.SerializedName
import com.hyeeyoung.wishboard.model.wish.WishItem

data class CartItem(
    val wishItem: WishItem,
    val cartItemInfo: CartItemInfo,
) {
    data class CartItemInfo(
        @SerializedName("item_count")
        val count: Int
    )
}
