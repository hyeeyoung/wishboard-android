package com.hyeeyoung.wishboard.presentation.noti.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.hyeeyoung.wishboard.domain.model.NotiItem
import com.hyeeyoung.wishboard.presentation.noti.types.NotiType
import java.time.LocalDate
import java.time.LocalDateTime

private const val PAGE_COUNT = Int.MAX_VALUE
private const val INITIAL_PAGE = PAGE_COUNT / 2

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Calendar(notiList: List<NotiItem>) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var prevPage by remember { mutableStateOf(INITIAL_PAGE) }
    val curMonthNoti = notiList.filter { it.notiDate.month == selectedDate.month }
    val curDateNoti = curMonthNoti.filter { it.notiDate.dayOfMonth == selectedDate.dayOfMonth }
    val pagerState = rememberPagerState(initialPage = INITIAL_PAGE)

    Column {
        CalendarHeader(selectedDate = selectedDate)
        CalendarTable(
            selectedDate = selectedDate,
            onSelect = { selectedDate = it },
            notiDateList = curMonthNoti.map { it.notiDate.toLocalDate() },
            pagerState = pagerState,
            pageCount = PAGE_COUNT,
            onChangePage = { page ->
                val diff = page - prevPage
                if (diff < 0) selectedDate = selectedDate.minusMonths(1)
                else if (diff > 0) selectedDate = selectedDate.plusMonths(1)
                prevPage = page
            })
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
                LocalDateTime.of(2023, 5, 18, 20, 0)
            ),
            NotiItem(
                1,
                "https://image.msscdn.net/images/goods_img/20220222/2377269/2377269_16777177260753_500.jpg",
                "W CLASSIC LOGO TEE white",
                "https://www.musinsa.com/app/goods/2377269",
                0,
                NotiType.RESTOCK,
                LocalDateTime.of(2023, 5, 27, 15, 0)
            ),
            NotiItem(
                1,
                "https://image.msscdn.net/images/goods_img/20220222/2377269/2377269_16777177260753_500.jpg",
                "W CLASSIC LOGO TEE white",
                "https://www.musinsa.com/app/goods/2377269",
                0,
                NotiType.RESTOCK,
                LocalDateTime.of(2023, 6, 7, 15, 0)
            ),
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
            ),
            NotiItem(
                2,
                "https://image.msscdn.net/images/goods_img/20230427/3267246/3267246_16825933559850_500.jpg",
                "체리 자카드 패턴 숏 슬리브 가디건 [핑크]",
                "https://www.musinsa.com/app/goods/3267246/0",
                0,
                NotiType.PREORDER,
                LocalDateTime.of(2023, 8, 10, 11, 0)
            ),
            NotiItem(
                2,
                "https://image.msscdn.net/images/goods_img/20230427/3267246/3267246_16825933559850_500.jpg",
                "체리 자카드 패턴 숏 슬리브 가디건 [핑크]",
                "https://www.musinsa.com/app/goods/3267246/0",
                0,
                NotiType.PREORDER,
                LocalDateTime.of(2023, 8, 11, 14, 0)
            ),
            NotiItem(
                2,
                "https://image.msscdn.net/images/goods_img/20230427/3267246/3267246_16825933559850_500.jpg",
                "체리 자카드 패턴 숏 슬리브 가디건 [핑크]",
                "https://www.musinsa.com/app/goods/3267246/0",
                0,
                NotiType.PREORDER,
                LocalDateTime.of(2023, 8, 22, 19, 0)
            )
        )
    )
}
