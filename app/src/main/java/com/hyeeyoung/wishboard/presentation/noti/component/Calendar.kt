package com.hyeeyoung.wishboard.presentation.noti.component

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.hyeeyoung.wishboard.domain.model.NotiItem
import com.hyeeyoung.wishboard.presentation.noti.types.NotiType
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun Calendar(notiList: List<NotiItem>) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val curMonthNoti = notiList.filter { it.notiDate.month == selectedDate.month }
    val curDateNoti = curMonthNoti.filter { it.notiDate.dayOfMonth == selectedDate.dayOfMonth }

    Column {
        CalendarHeader(selectedDate = selectedDate)
        CalendarTable(
            selectedDate = selectedDate,
            onSelect = { selectedDate = it },
            notiDateList = curMonthNoti.map { it.notiDate.toLocalDate() })
        CalendarSchedule(selectedDate = selectedDate, notiItems = curDateNoti)
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarPreview() {
    Calendar(
        listOf(
            NotiItem(
                1,
                "https://image.msscdn.net/images/goods_img/20220222/2377269/2377269_16777177260753_500.jpg",
                "W CLASSIC LOGO TEE white",
                "https://www.musinsa.com/app/goods/2377269",
                0,
                NotiType.RESTOCK,
                LocalDateTime.of(2023, 7, 3, 13, 30)
            ),
            NotiItem(
                2,
                "https://image.msscdn.net/images/goods_img/20230427/3267246/3267246_16825933559850_500.jpg",
                "체리 자카드 패턴 숏 슬리브 가디건 [핑크]",
                "https://www.musinsa.com/app/goods/3267246/0",
                0,
                NotiType.PREORDER,
                LocalDateTime.of(2023, 7, 20, 0, 0)
            )
        )
    )
}
