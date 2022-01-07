package com.hyeeyoung.wishboard.util

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.DecimalFormat

@BindingAdapter("priceFormat")
fun TextView.setPriceFormat(price: Number?) {
    val decimalFormat = DecimalFormat("#,###")
    text = decimalFormat.format(price ?: 0)
}