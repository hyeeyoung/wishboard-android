package com.hyeeyoung.wishboard.domain.entity

import android.os.Parcelable
import com.hyeeyoung.wishboard.presentation.noti.types.NotiType
import kotlinx.parcelize.Parcelize

@Parcelize
data class WishItemDetail(
    val createAt: String,
    val folderId: Long?,
    val folderName: String?,
    val id: Long,
    val image: String?,
    val memo: String?,
    val name: String,
    val notiDate: String?,
    val notiType: NotiType?,
    val price: String,
    val site: String?
) : Parcelable