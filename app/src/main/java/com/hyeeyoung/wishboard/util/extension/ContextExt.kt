package com.hyeeyoung.wishboard.util.extension

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.hyeeyoung.wishboard.R

fun Context.showToast(message: String, isShort: Boolean = true) {
    val duration = if (isShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
    Toast.makeText(this, message, duration).show()
}

fun Context.sendMail(title: String, content: String) {
    Intent(Intent.ACTION_SEND).apply {
        type = "plain/text"
        putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.wishboard_email)))
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, content)
    }.also { startActivity(it) }
}