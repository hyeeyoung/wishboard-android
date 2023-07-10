package com.hyeeyoung.wishboard.util.extension

import com.hyeeyoung.wishboard.presentation.noti.types.NotiType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

fun String?.toPlainRequestBody(): RequestBody =
    requireNotNull(this).toRequestBody("text/plain".toMediaTypeOrNull())

fun String?.toPlainNullableRequestBody(): RequestBody? =
    this?.toRequestBody("text/plain".toMediaTypeOrNull())

fun String.toNotiType() =
    when (this) {
        NotiType.RESTOCK.str -> NotiType.RESTOCK
        NotiType.OPEN.str -> NotiType.OPEN
        NotiType.PREORDER.str -> NotiType.PREORDER
        NotiType.SALE_CLOSE.str -> NotiType.SALE_CLOSE
        NotiType.SALE_START.str -> NotiType.SALE_START
        NotiType.REMIND.str -> NotiType.REMIND
        else -> throw IllegalArgumentException("유효하지 않은 알림 유형입니다.")
    }
