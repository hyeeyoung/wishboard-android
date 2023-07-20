package com.hyeeyoung.wishboard.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

inline fun <T1 : Any, T2 : Any, R : Any> safeLet(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}

inline fun <T1 : Any, T2 : Any, T3 : Any, R : Any> safeLet(
    p1: T1?,
    p2: T2?,
    p3: T3?,
    block: (T1, T2, T3) -> R?
): R? {
    return if (p1 != null && p2 != null && p3 != null) block(p1, p2, p3) else null
}

inline fun View.setOnSingleClickListener(
    delay: Long = 500L,
    crossinline block: (View) -> Unit
) {
    var previousClickedTime = 0L
    setOnClickListener { view ->
        val clickedTime = System.currentTimeMillis()
        if (clickedTime - previousClickedTime >= delay) {
            block(view)
            previousClickedTime = clickedTime
        }
    }
}

fun getTimestamp(): String {
    val pattern = "yyyyMMdd'T'HHmmss'Z'"
    val timeZone = TimeZone.getTimeZone("UTC")
    val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    dateFormat.timeZone = timeZone
    return dateFormat.format(Date())
}

fun getNotiDateServerFormat(date: String, hour: String, minute: String) =
    "$date $hour:$minute:00" // TODO DateFormatUtil.kt 로 이동

fun showKeyboard(context: Context, view: View, toShow: Boolean) {
    val imm: InputMethodManager =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    if (toShow) {
        imm.showSoftInput(view, 0)
    } else {
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

/** 이미지 url을 bitmap으로 변환 */
suspend fun getBitmapFromURL(imageUrl: String): Bitmap? {
    val bitmap = try {
        val url = URL(imageUrl)

        withContext(Dispatchers.IO) {
            val connection = (url.openConnection() as? HttpURLConnection)?.apply {
                doInput = true
                connect()
            }
            connection?.let { BitmapFactory.decodeStream(it.inputStream) }
        }
    } catch (e: Exception) {
        Timber.e(e.message)
        null
    }
    return bitmap
}

fun getFileFromBitmap(bitmap: Bitmap, token: String, context: Context): File? {
    val cache = context.externalCacheDir
    val shareFile = File(cache, makePhotoFileName(token))
    Timber.d("파일 경로: ${shareFile.absolutePath}")

    return try {
        val out = FileOutputStream(shareFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.flush()
        out.close()
        shareFile
    } catch (e: Exception) {
        Timber.e(e.message)
        null
    }
}

/** 이미지 파일명 생성하는 함수 */
fun makePhotoFileName(token: String): String {
    val timestamp = getTimestamp()
    return ("${token.substring(7)}_${timestamp}.jpg")
}