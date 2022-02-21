package com.hyeeyoung.wishboard.util

import android.graphics.Color
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hyeeyoung.wishboard.util.custom.CustomDecoration
import java.text.DecimalFormat

@BindingAdapter("priceFormat")
fun TextView.setPriceFormat(price: Number?) {
    val decimalFormat = DecimalFormat("#,###")
    text = decimalFormat.format(price ?: 0)
}

@BindingAdapter(value = ["dividerHeight", "dividerPadding", "dividerColor"], requireAll = false)
fun RecyclerView.setDivider(
    dividerHeight: Float?,
    dividerPadding: Float?,
    @ColorInt dividerColor: Int?
) {
    val decoration = CustomDecoration(
        height = dividerHeight ?: 0f,
        padding = dividerPadding ?: 0f,
        color = dividerColor ?: Color.TRANSPARENT
    )
    addItemDecoration(decoration)
}