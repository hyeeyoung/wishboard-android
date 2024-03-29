package com.hyeeyoung.wishboard.presentation.calendar.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.designsystem.component.WishboardDivider
import com.hyeeyoung.wishboard.designsystem.theme.Gray700
import com.hyeeyoung.wishboard.designsystem.theme.Green200
import com.hyeeyoung.wishboard.designsystem.theme.Green500
import com.hyeeyoung.wishboard.designsystem.theme.WishBoardTheme
import com.hyeeyoung.wishboard.util.extension.noRippleClickable
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.ceil

private const val COL_SIZE = 7

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarTable(
    selectedDate: LocalDate,
    onSelect: (LocalDate) -> Unit,
    notiDateList: List<LocalDate>,
    pagerState: PagerState,
    pageCount: Int,
    onChangePage: (Int) -> Unit
) {
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onChangePage(page)
        }
    }

    Column() {
        WishboardDivider()
        HorizontalPager(pageCount = pageCount, state = pagerState) {
            DateTable(selectedDate, onSelect, notiDateList)
        }
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
            modifier = modifier.noRippleClickable { onSelect(date) }
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

@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true, widthDp = 375)
@Composable
fun CalendarTablePreview() {
    CalendarTable(
        selectedDate = LocalDate.now(),
        onSelect = {},
        notiDateList = listOf(LocalDate.of(2023, 7, 3), LocalDate.of(2023, 7, 20)),
        pagerState = rememberPagerState(),
        pageCount = 1,
        onChangePage = {}
    )
}