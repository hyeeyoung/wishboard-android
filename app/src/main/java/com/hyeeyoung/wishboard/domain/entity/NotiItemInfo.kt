package com.hyeeyoung.wishboard.domain.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hyeeyoung.wishboard.presentation.noti.types.NotiType
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotiItemInfo( // TODO 네이밍 수정 필요
    val itemId: Long,
    val itemImg: String? = null,
    val itemName: String,
    val itemUrl: String? = null,
    var readState: Int,
    val notiType: NotiType,
    val notiDate: String,
) : Parcelable