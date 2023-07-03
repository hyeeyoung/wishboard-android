package com.hyeeyoung.wishboard.presentation.noti.component

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import java.time.LocalDate

@Composable
fun CalendarScreen() {
    Column {
        CalendarHeader(localDate = LocalDate.now())
        CalendarTable(localDate = LocalDate.now())
        CalendarSchedule(selectedDate = LocalDate.now(), notiItems = listOf())
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarPreview() {
    CalendarScreen()
}
