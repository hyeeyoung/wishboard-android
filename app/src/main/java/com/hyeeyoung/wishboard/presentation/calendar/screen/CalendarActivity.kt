package com.hyeeyoung.wishboard.presentation.calendar.screen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.hyeeyoung.wishboard.presentation.noti.NotiViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CalendarActivity : ComponentActivity() {
    private val viewModel: NotiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchCalendarNotiList()

        setContent {
            val notiList by viewModel.calendarNotiList.collectAsState()

            CalendarScreen(
                notiList = notiList,
                onClickBack = { finish() },
                onClickNotiWithLink = { shopUrl ->
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(shopUrl)))
                }
            )
        }
    }
}
