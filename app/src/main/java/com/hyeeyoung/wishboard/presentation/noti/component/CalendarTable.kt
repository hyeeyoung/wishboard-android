package com.hyeeyoung.wishboard.presentation.noti.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
fun CalendarTable(
    selectedDate: LocalDate,
    onSelect: (LocalDate) -> Unit,
    notiDateList: List<LocalDate>
) {
    Column() {
        WishboardDivider()
        DateTable(selectedDate, onSelect, notiDateList)
        WishboardDivider()
    }
}

/** 해당 월의 날짜 테이블 */
@Composable
fun DateTable(
    selectedDate: LocalDate,
    onSelect: (LocalDate) -> Unit,
    notiDateList: List<LocalDate>
) {
    var day = 1
    val firstIdx = selectedDate.withDayOfMonth(1).dayOfWeek.value % 7
    val lastDay = YearMonth.from(selectedDate).atEndOfMonth().dayOfMonth
    val rowSize = ceil((firstIdx + lastDay) / 7.0).toInt()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cellSize = screenWidth / COL_SIZE
    val dateCellModifier = Modifier.size(cellSize)

    Column() {
        repeat(rowSize) { r ->
            Row() {
                repeat(COL_SIZE) { c ->
                    if (day > lastDay) return
                    val dateOrNull =
                        if (r != 0 || c >= firstIdx) LocalDate.of(
                            selectedDate.year,
                            selectedDate.monthValue,
                            day
                        )
                        else null
                    DateCell(
                        modifier = dateCellModifier,
                        date = dateOrNull,
                        selected = dateOrNull == selectedDate,
                        onSelect = onSelect,
                        isExistNoti = dateOrNull in notiDateList
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
        Box(
            modifier = modifier.clickable { onSelect(date) }
        ) {
            if (isExistNoti) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(9.dp)
                ) {
                    drawCircle(color = Green200)
                }
            }

            val isToday = LocalDate.now() == date
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

            val textStyle = WishBoardTheme.typography.suitD1.run {
                TextStyle(
                    fontFamily = fontFamily,
                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Normal,
                    fontSize = fontSize
                )
            }

            Text(
                modifier = Modifier
                    .padding(9.dp)
                    .align(Alignment.Center),
                text = date.dayOfMonth.toString(),
                color = Gray700,
                style = textStyle
            )
        }
    } else {
        Spacer(modifier = modifier)
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
fun CalendarTablePreview() {
    CalendarTable(
        LocalDate.now(),
        {},
        listOf(LocalDate.of(2023, 7, 3), LocalDate.of(2023, 7, 20))
    )
}