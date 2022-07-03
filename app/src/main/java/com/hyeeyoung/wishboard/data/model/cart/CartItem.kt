package com.hyeeyoung.wishboard.data.model.cart

import com.google.gson.annotations.SerializedName
import com.hyeeyoung.wishboard.data.model.wish.WishItem

data class CartItem(
    var wishItem: WishItem,
    val cartItemInfo: CartItemInfo,
) {
    data class CartItemInfo(
        @SerializedName("item_count")
        var count: Int
    )
}
