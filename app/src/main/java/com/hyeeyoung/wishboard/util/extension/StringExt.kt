package com.hyeeyoung.wishboard.util.extension

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

fun String?.toPlainRequestBody(): RequestBody =
    requireNotNull(this).toRequestBody("text/plain".toMediaTypeOrNull())

fun String?.toPlainNullableRequestBody(): RequestBody? =
    this?.toRequestBody("text/plain".toMediaTypeOrNull())