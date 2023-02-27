package com.hyeeyoung.wishboard.data.model.noti

import com.google.gson.annotations.SerializedName
import com.hyeeyoung.wishboard.domain.model.NotiItemInfo
import com.hyeeyoung.wishboard.presentation.noti.types.NotiType

data class NotiItem(
    @SerializedName("item_id")
    val itemId: Long,
    @SerializedName("item_img_url")
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
) {
    fun toNotiItemInfo() = NotiItemInfo(
        itemId,
        itemImg,
        itemName,
        if (itemUrl.isNullOrBlank()) null else itemUrl,
        readState,
        notiType,
        notiDate,
    )
}