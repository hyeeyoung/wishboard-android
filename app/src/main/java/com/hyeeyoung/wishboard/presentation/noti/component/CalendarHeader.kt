package com.hyeeyoung.wishboard.presentation.noti.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.presentation.theme.Gray700
import com.hyeeyoung.wishboard.presentation.theme.WishBoardTheme
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.*

@Composable
fun CalendarHeader(selectedDate: LocalDate, onClickBack: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CalendarTopAppBar(month = selectedDate.month, year = selectedDate.year, onClickBack)
        Spacer(modifier = Modifier.height(2.dp))
        DayOfTheWeekLabel()
        Spacer(modifier = Modifier.height(12.dp))
    }
}

/** 월, 년도 정보, 백버튼을 포함하는 캘린더 타이틀 라벨 */
@Composable
fun CalendarTopAppBar(month: Month, year: Int, onClickBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.align(Alignment.CenterStart)) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(modifier = Modifier
                .clickable { onClickBack() }
                .padding(9.dp),
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null)
        }
        Text(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = 10.dp),
            text = "${month.getDisplayName(TextStyle.FULL, Locale.US)} $year",
            color = Gray700,
            style = WishBoardTheme.typography.montserratH1
        )
    }
}

/** 월 - 일요일까지의 모든 요일 라벨 */
@Composable
fun DayOfTheWeekLabel() {
    // TODO SpaceEvenly 사용
    val days = listOf("Sun", "Mon", "Tue", "Wen", "Thu", "Fri", "Sat")
    Row(modifier = Modifier.fillMaxWidth()) {
        days.forEach { day ->
            Text(
                modifier = Modifier.weight(1.0f),
                text = day,
                color = Gray700,
                textAlign = TextAlign.Center,
                style = WishBoardTheme.typography.montserratB2
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarHeaderPreview() {
    CalendarHeader(LocalDate.now(), {})
}