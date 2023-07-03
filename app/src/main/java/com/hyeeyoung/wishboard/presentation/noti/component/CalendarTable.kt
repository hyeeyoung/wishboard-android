package com.hyeeyoung.wishboard.presentation.noti.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.presentation.common.component.WishboardDivider
import com.hyeeyoung.wishboard.presentation.theme.Gray700
import com.hyeeyoung.wishboard.presentation.theme.Green200
import com.hyeeyoung.wishboard.presentation.theme.Green500
import com.hyeeyoung.wishboard.presentation.theme.WishBoardTheme
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.ceil

private const val COL_SIZE = 7

@Composable
fun CalendarTable(selectedDate: LocalDate) {
    Column() {
        WishboardDivider()
        DateTable(selectedDate)
        WishboardDivider()
    }
}

/** 해당 월의 날짜 테이블 */
@Composable
fun DateTable(date: LocalDate) {
    var day = 1
    val firstIdx = date.withDayOfMonth(1).dayOfWeek.value % 7
    val lastDay = YearMonth.from(date).atEndOfMonth().dayOfMonth
    val rowSize = ceil((firstIdx + lastDay) / 7.0).toInt()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cellSize = screenWidth / COL_SIZE
    val dateCellModifier = Modifier.size(cellSize)
    var selectedDate by remember { mutableStateOf(date) }

    Column() {
        repeat(rowSize) { r ->
            Row() {
                repeat(COL_SIZE) { c ->
                    if (day > lastDay) return
                    val dateOrNull =
                        if (r != 0 || c >= firstIdx) LocalDate.of(
                            date.year,
                            date.monthValue,
                            day
                        )
                        else null
                    DateCell(
                        modifier = dateCellModifier,
                        date = dateOrNull,
                        selected = dateOrNull == selectedDate,
                        onSelect = { selectedDate = it },
                        isExistNoti = day in listOf(18, 19, 30) // TODO db 데이터 연동
                    )
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
    onSelect: (LocalDate) -> Unit,
    isExistNoti: Boolean = false
) {
    if (date != null) {
        val isToday = LocalDate.now() == date
        val boxModifier = modifier.then(Modifier.clickable { onSelect(date) }).let {
            if (isExistNoti) it.then(
                Modifier
                    .padding(10.dp)
                    .background(color = Green200, shape = CircleShape)
            ) else it
        }

        Box(
            modifier = boxModifier
        ) {
            if (isToday) {
                Canvas(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(10.dp)
                        .align(Alignment.TopCenter)
                ) {
                    drawCircle(color = Green500)
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
    CalendarTable(LocalDate.now())
}