package com.hyeeyoung.wishboard.model.wish

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WishItem (
    @SerializedName("folder_id")
    val folderId: Long? = null,
    @SerializedName("folder_name")
    val folderName: String? = null,
    @SerializedName("item_id")
    val id: Long? = null,
    @SerializedName("item_img")
    val image: String,
    @SerializedName("item_name")
    val name: String,
    @SerializedName("item_price")
    val price: Int? = null,
    @SerializedName("item_url")
    val url: String?= null,
    @SerializedName("item_memo")
    val memo: String? = null,
    @SerializedName("create_at")
    val createAt: String? = null,
    @SerializedName("item_notification_type")
    val notiType: String? = null,
    @SerializedName("item_notification_date")
    val notiDate: String? = null,
    @SerializedName("cart_item_id")
    val cartId: Long? = null,
): Parcelable
