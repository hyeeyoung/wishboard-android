package com.hyeeyoung.wishboard.model.noti

import com.hyeeyoung.wishboard.R

enum class NotiType(val strRes: Int) {
    NONE(R.string.select_notification_type),
    RESTOCK(R.string.restock),
    OPEN_DAY(R.string.open_day),
    PREORDER_CLOSE(R.string.preorder_close),
    SALE_START(R.string.sale_start),
    SALE_CLOSE(R.string.sale_close)
}