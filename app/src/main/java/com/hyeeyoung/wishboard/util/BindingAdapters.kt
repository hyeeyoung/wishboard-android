package com.hyeeyoung.wishboard.util

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.util.custom.CustomDecoration
import java.text.DecimalFormat

@BindingAdapter("imageUrl")
fun ImageView.setImageUrl(imageUrl: String?) {
    load(imageUrl) {
        placeholder(R.color.gray_150)
    }
}

@BindingAdapter("priceFormat")
fun TextView.setPriceFormat(price: Number?) {
    val decimalFormat = DecimalFormat("#,###")
    text = decimalFormat.format(price ?: 0)
}

@BindingAdapter("priceFormat")
fun TextView.setPriceFormat(price: String?) {
    val decimalFormat = DecimalFormat("#,###")
    text = decimalFormat.format(price?.toIntOrNull() ?: 0)
}

@BindingAdapter("timeFormat")
fun TextView.setTimeFormat(regTime: Long?) {
    if (regTime == null || regTime == 0L) run {
        text = ""
        return
    }

    val curTime = System.currentTimeMillis()
    var diffTime = (curTime - regTime) / 1000

    text = when {
        diffTime < TimeUtil.SEC -> {
            context.getString(R.string.time_a_moment_ago)
        }
        TimeUtil.SEC.let { diffTime /= it; diffTime } < TimeUtil.MIN -> {
            diffTime.toString() + context.getString(R.string.time_minutes_ago)
        }
        TimeUtil.MIN.let { diffTime /= it; diffTime } < TimeUtil.HOUR -> {
            diffTime.toString() + context.getString(R.string.time_hours_ago)
        }
        TimeUtil.HOUR.let { diffTime /= it; diffTime } < TimeUtil.DAY_OF_MONTH -> {
            if (diffTime < TimeUtil.DAY_OF_WEEK) {
                diffTime.toString() + context.getString(R.string.time_days_ago)
            } else {
                diffTime /= TimeUtil.DAY_OF_WEEK
                diffTime.toString() + context.getString(R.string.time_weeks_ago)
            }
        }
        TimeUtil.DAY_OF_MONTH.let { diffTime /= it; diffTime } < TimeUtil.MONTH -> {
            diffTime.toString() + context.getString(R.string.time_months_ago)
        }
        else -> {
            diffTime /= TimeUtil.MONTH
            diffTime.toString() + context.getString(R.string.time_years_ago)
        }
    }
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