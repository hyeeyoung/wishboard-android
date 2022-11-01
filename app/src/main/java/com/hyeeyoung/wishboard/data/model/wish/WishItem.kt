package com.hyeeyoung.wishboard.data.model.wish

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hyeeyoung.wishboard.presentation.noti.types.NotiType
import kotlinx.parcelize.Parcelize

@Parcelize
data class WishItem(
    @SerializedName("folder_id")
    var folderId: Long? = null,
    @SerializedName("folder_name")
    var folderName: String? = null,
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
    @SerializedName("item_url")
    val url: String? = null,
    @SerializedName("item_memo")
    val memo: String? = null,
    @SerializedName("create_at")
    val createAt: String? = null,
    @SerializedName("item_notification_type")
    val notiType: NotiType? = null,
    @SerializedName("item_notification_date")
    val notiDate: String? = null,
    /** cartState(1) : 장바구니에 존재, cartState(0) : 장바구니에 존재 X*/
    @SerializedName("cart_state")
    var cartState: Int? = null,
) : Parcelable {
    companion object {
        fun from(wishItem: WishItem): WishItem {
            val folderId = wishItem.folderId
            val folderName = wishItem.folderName
            val id = wishItem.id
            val image = wishItem.imageUrl
            val name = wishItem.name
            val price = wishItem.price
            val url = wishItem.url
            val memo = wishItem.memo
            val createAt = wishItem.createAt
            val notiType = wishItem.notiType
            val notiDate = wishItem.notiDate
            var cartState = wishItem.cartState // TODO CartStateType으로 타입 변환 고려

            return WishItem(
                folderId = folderId,
                folderName = folderName,
                id = id,
                imageUrl = image,
                name = name,
                price = price,
                url = url,
                memo = memo,
                createAt = createAt,
                notiType = notiType,
                notiDate = notiDate,
                cartState = cartState
            )
        }
    }
}
