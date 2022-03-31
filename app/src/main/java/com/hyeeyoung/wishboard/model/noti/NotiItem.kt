package com.hyeeyoung.wishboard.model.noti

import com.google.gson.annotations.SerializedName

data class NotiItem(
    @SerializedName("item_id")
    val itemId: Long,
    @SerializedName("item_img")
    val itemImg: String? = null, // TODO rename itemImage
    var itemImageUrl: String? = null,
    @SerializedName("item_name")
    val itemName: String,
    @SerializedName("item_url")
    val itemUrl: String? = null,
    @SerializedName("read_state")
    var readState: Int,
    @SerializedName("item_notification_type")
    val notiType: NotiType,
    @SerializedName("item_notification_date")
    val notiDate: String,
)