package com.hyeeyoung.wishboard.presentation.noti.types

import com.hyeeyoung.wishboard.R

enum class NotiType(val strRes: Int) {
    RESTOCK(R.string.restock),
    OPEN(R.string.open_day),
    PREORDER(R.string.preorder),
    SALE_START(R.string.sale_start),
    SALE_CLOSE(R.string.sale_close)
}