package com.hyeeyoung.wishboard.model.cart

import com.google.gson.annotations.SerializedName
import com.hyeeyoung.wishboard.model.wish.WishItem

data class CartItem(
    var wishItem: WishItem,
    val cartItemInfo: CartItemInfo,
) {
    data class CartItemInfo(
        @SerializedName("item_count")
        var count: Int
    )
}
