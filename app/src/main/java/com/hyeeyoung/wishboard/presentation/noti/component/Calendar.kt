package com.hyeeyoung.wishboard.presentation.noti.component

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import java.time.LocalDate

@Composable
fun Calendar() {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Column {
        CalendarHeader(selectedDate = selectedDate)
        CalendarTable(selectedDate = selectedDate)
        CalendarSchedule(selectedDate = selectedDate, notiItems = listOf())
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarPreview() {
    Calendar()
}
