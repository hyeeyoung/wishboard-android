package com.hyeeyoung.wishboard.util.extension

import android.content.res.Resources

val Int.px get() = (this * Resources.getSystem().displayMetrics.density)