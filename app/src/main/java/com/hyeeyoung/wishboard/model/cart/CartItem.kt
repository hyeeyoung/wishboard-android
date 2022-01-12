package com.hyeeyoung.wishboard.model.cart

import com.google.gson.annotations.SerializedName
import com.hyeeyoung.wishboard.model.wish.WishItem

data class CartItem(
    val wishItem: WishItem,
    @SerializedName("cartItemCount") // TODO 서버에서 변수명 수정 필요, 수정 사항 반영되면 해당 라인 삭제 예정
    val cartItemInfo: CartItemInfo,
) {
    data class CartItemInfo(
        @SerializedName("item_count")
        val count: Int
    )
}
