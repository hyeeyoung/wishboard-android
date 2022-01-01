package com.hyeeyoung.wishboard.model.wish

import com.google.gson.annotations.SerializedName

data class WishItemInfo (
    @SerializedName("folder_id")
    val folderId: Int?,
    @SerializedName("folder_name")
    val folderName: String?,
    @SerializedName("item_img")
    val image: String,
    @SerializedName("item_name")
    val name: String,
    @SerializedName("item_price")
    val price: String,
    @SerializedName("item_memo")
    val memo: String?,
    @SerializedName("item_url")
    val itemUrl: String,
    @SerializedName("create_at")
    val createAt: String,
    @SerializedName("item_notification_type")
    val notiType: String?,
    @SerializedName("item_notification_date")
    val notiDate: String?,
)
