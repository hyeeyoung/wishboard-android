package com.hyeeyoung.wishboard.presentation.noti.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.presentation.common.component.WishboardDivider
import com.hyeeyoung.wishboard.presentation.theme.Gray700
import com.hyeeyoung.wishboard.presentation.theme.Green500
import com.hyeeyoung.wishboard.presentation.theme.WishBoardTheme
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.ceil

private const val WEEK = 7

@Composable
fun CalendarTable(localDate: LocalDate, width: Dp) {
    Column() {
        WishboardDivider()
        DateTable(width, localDate)
        WishboardDivider()
    }
}

/** 해당 월의 날짜 테이블 */
@Composable
fun DateTable(width: Dp, selectedDate: LocalDate) {
    var day = 1
    val firstIdx = selectedDate.withDayOfMonth(1).dayOfWeek.value % 7
    val lastDay = YearMonth.from(selectedDate).atEndOfMonth().dayOfMonth
    val rowSize = ceil((firstIdx + lastDay) / 7.0).toInt()
    val cellSize = width / WEEK

    Column() {
        repeat(rowSize) { r ->
            Row() {
                val modifier = Modifier.size(cellSize)
                repeat(WEEK) { c ->
                    if (day > lastDay) return
                    val dateOrNull =
                        if (r != 0 || c >= firstIdx) LocalDate.of(
                            selectedDate.year,
                            selectedDate.monthValue,
                            day
                        )
                        else null
                    DateCell(modifier = modifier, date = dateOrNull)
                    if (dateOrNull != null) day++
                }
            }
        }
    }
}

/** 날짜 */
@Composable
fun DateCell(
    modifier: Modifier,
    date: LocalDate?,
    selected: Boolean = false,
    isExistNoti: Boolean = false
) {
    if (date != null) {
        val isToday = LocalDate.now() == date
        // TODO 알림 존재 여부, 오늘 여부, 클릭 여부에 따른 ui 업데이트 처리
        Box(modifier = modifier) {
            if (isToday) {
                Column(modifier = Modifier.align(Alignment.TopCenter)) {
                    Spacer(modifier = Modifier.size(6.dp))
                    Canvas(modifier = Modifier.size(10.dp)) {
                        drawCircle(color = Green500)
                    }
                }
            }
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = date.dayOfMonth.toString(),
                color = Gray700,
                textAlign = TextAlign.Center,
                style = WishBoardTheme.typography.suitD1,
                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Normal
            )
        }
    } else {
        Spacer(modifier = modifier)
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
fun CalendarTablePreview() {
    CalendarTable(LocalDate.now(), 375.dp)
}