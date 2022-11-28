package com.hyeeyoung.wishboard.data.model.wish

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hyeeyoung.wishboard.presentation.noti.types.NotiType
import kotlinx.parcelize.Parcelize

@Parcelize
data class WishItem( // TODO 네이밍 변경
    @SerializedName("item_id")
    val id: Long? = null,
    @SerializedName("item_img")
    var image: String? = null,
    @SerializedName("item_img_url") // TODO need refactoring
    var imageUrl: String? = null,
    @SerializedName("item_name")
    val name: String,
    @SerializedName("item_price")
    val price: Int? = null,
    /** cartState(1) : 장바구니에 존재, cartState(0) : 장바구니에 존재 X*/
    @SerializedName("cart_state")
    var cartState: Int? = null,
) : Parcelable
