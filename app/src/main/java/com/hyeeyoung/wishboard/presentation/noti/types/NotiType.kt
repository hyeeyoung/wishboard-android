package com.hyeeyoung.wishboard.presentation.noti.types

enum class NotiType(val str: String) {
    RESTOCK("재입고"),
    OPEN("오픈"),
    PREORDER("프리오더"),
    SALE_START("세일 시작"),
    SALE_CLOSE("세일 마감"),
    REMIND("리마인드")
}