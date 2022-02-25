package com.hyeeyoung.wishboard.util

import android.widget.ImageView
import androidx.lifecycle.LifecycleCoroutineScope
import com.bumptech.glide.Glide
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.remote.AWSS3Service
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.Locale
import java.util.Date

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

/** S3에서 다운로드 받은 이미지를 ImageView에 디스플레이 */
fun loadProfileImage( // TODO need refactoring
    lifecycleScope: LifecycleCoroutineScope,
    imageUrl: String,
    imageView: ImageView
) {
    lifecycleScope.launch {
        AWSS3Service().getImageUrl(imageUrl)?.let { imageUrl ->
            Glide.with(imageView.context).load(imageUrl)
                .placeholder(R.drawable.ic_background_user_profile).into(imageView)
        }
    }
}

fun getNotiDateServerFormat(date: String, hour: String, minute: String) =
    "$date $hour:$minute:00" // TODO DateFormatUtil.kt 로 이동

/** 닉네임이 없는 유저는 이메일 hashcode에서 앞부분 6자리로 임시 부여 */
fun getTempNicknameFormat(): String =
    "wish${prefs?.getUserEmail().hashCode().toString().substring(0, 6)}"