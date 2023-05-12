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
        else -> NotiType.SALE_START
    }
