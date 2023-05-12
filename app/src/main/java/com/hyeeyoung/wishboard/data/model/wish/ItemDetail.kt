package com.hyeeyoung.wishboard.data.model.wish

import com.google.gson.annotations.SerializedName
import com.hyeeyoung.wishboard.domain.model.WishItemDetail
import com.hyeeyoung.wishboard.util.extension.toNotiType

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
    val notiType: String?,
    @SerializedName("item_price")
    val price: String,
    @SerializedName("item_url")
    val site: String?
) {
    fun toWishItemDetail() = WishItemDetail(
        createAt,
        folderId,
        if (folderName.isNullOrBlank()) null else folderName,
        id,
        image,
        if (memo.isNullOrBlank()) null else memo,
        name,
        notiDate,
        notiType?.toNotiType(),
        price,
        if (site.isNullOrBlank()) null else site
    )
}