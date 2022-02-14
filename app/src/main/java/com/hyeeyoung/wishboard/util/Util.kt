package com.hyeeyoung.wishboard.util

import android.widget.ImageView
import androidx.lifecycle.LifecycleCoroutineScope
import com.bumptech.glide.Glide
import com.hyeeyoung.wishboard.remote.AWSS3Service
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

var prefs: PreferenceUtil? = null

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

fun getTimestamp(): String {
    val pattern = "yyyyMMdd'T'HHmmss'Z'"
    val timeZone = TimeZone.getTimeZone("UTC")
    val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    dateFormat.timeZone = timeZone
    return dateFormat.format(Date())
}

/** S3에서 다운로드 받은 이미지를 ImageView에 디스플레이 */
fun loadImage(
    lifecycleScope: LifecycleCoroutineScope,
    imageUrl: String,
    imageView: ImageView
) {
    lifecycleScope.launch {
        AWSS3Service().getImageUrl(imageUrl)?.let { imageUrl ->
            Glide.with(imageView.context).load(imageUrl).into(imageView)
        }
    }
}