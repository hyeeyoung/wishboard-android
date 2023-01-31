package com.hyeeyoung.wishboard.data.model.wish

import com.google.gson.annotations.SerializedName
import com.hyeeyoung.wishboard.domain.model.WishItemDetail
import com.hyeeyoung.wishboard.presentation.noti.types.NotiType

data class ItemDetail(
    @SerializedName("create_at")
    val createAt: String,
    @SerializedName("folder_id")
    val folderId: Long?,
    @SerializedName("folder_name")
    val folderName: String?,
    @SerializedName("item_id")
    val id: Long,
    @SerializedName("item_img_url")
    val image: String?,
    @SerializedName("item_memo")
    val memo: String?,
    @SerializedName("item_name")
    val name: String,
    @SerializedName("item_notification_date")
    val notiDate: String?,
    @SerializedName("item_notification_type")
    val notiType: NotiType?,
    @SerializedName("item_price")
    val price: String,
    @SerializedName("item_url")
    val site: String?
) {
    fun toWishItemDetail(detail: ItemDetail) = WishItemDetail(
        detail.createAt,
        detail.folderId,
        if (detail.folderName.isNullOrBlank()) null else detail.folderName,
        detail.id,
        detail.image,
        if (detail.memo.isNullOrBlank()) null else detail.memo,
        detail.name,
        detail.notiDate,
        detail.notiType,
        detail.price,
        if (detail.site.isNullOrBlank()) null else detail.site
    )
}