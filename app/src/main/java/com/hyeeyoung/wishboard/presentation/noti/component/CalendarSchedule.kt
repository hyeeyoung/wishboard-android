package com.hyeeyoung.wishboard.presentation.noti.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.domain.model.NotiItem
import com.hyeeyoung.wishboard.presentation.common.component.ColoredImage
import com.hyeeyoung.wishboard.presentation.noti.types.NotiType
import com.hyeeyoung.wishboard.presentation.theme.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoField

@Composable
fun CalendarSchedule(
    selectedDate: LocalDate,
    notiItems: List<NotiItem>,
    moveToShop: (String) -> Unit
) {
    Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp)) {
        Text(
            text = stringResource(
                id = R.string.noti_calendar_schedule_title,
                selectedDate.monthValue, selectedDate.dayOfMonth
            ),
            color = Gray700,
            style = WishBoardTheme.typography.suitH4
        )
        if (notiItems.isEmpty()) {
            EmptySchedule(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .weight(1.0f)
            )
        } else {
            Spacer(modifier = Modifier.size(16.dp)) // TODO dimensionResource 사용
            LazyColumn(
                modifier = Modifier
                    .weight(1.0f),
                contentPadding = PaddingValues(bottom = 64.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(notiItems) { noti ->
                    ScheduleItem(noti = noti, moveToShop = moveToShop)
                }
            }
        }
    }
}

@Composable
fun EmptySchedule(modifier: Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_noti_large),
            contentDescription = null
        )
        Spacer(modifier = Modifier.size(20.dp))
        // TODO lineHeight 정의
        Text(
            text = stringResource(id = R.string.noti_no_item_view),
            textAlign = TextAlign.Center,
            color = Gray200,
            style = WishBoardTheme.typography.suitB3
        )
    }
}

@Composable
fun ScheduleItem(noti: NotiItem, moveToShop: (String) -> Unit) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { noti.itemUrl?.let { moveToShop(it) } }
            .background(color = Gray50, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        val (image, notiType, notiDate, itemName) = createRefs()
        ColoredImage(
            modifier = Modifier
                .constrainAs(image) { start.linkTo(parent.start) }
                .size(72.dp)
                .clip(CircleShape),
            model = noti.itemImg,
        )
        Text(
            modifier = Modifier.constrainAs(notiType) {
                start.linkTo(image.end, margin = 10.dp)
                top.linkTo(parent.top)
            },
            text = stringResource(id = R.string.noti_item_type, noti.notiType.str),
            color = Gray700,
            style = WishBoardTheme.typography.suitH5
        )
        Text(
            modifier = Modifier.constrainAs(itemName) {
                top.linkTo(notiType.bottom, margin = 8.dp)
                start.linkTo(notiType.start)
            },
            text = noti.itemName,
            color = Gray700,
            style = WishBoardTheme.typography.suitD3,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            modifier = Modifier.constrainAs(notiDate) {
                bottom.linkTo(parent.bottom)
                start.linkTo(notiType.start)
            },
            text = getScheduleTimeFormat(noti.notiDate),
            color = Gray300,
            style = WishBoardTheme.typography.suitD3
        )
    }
}

// TODO 스트링 리소스 사용하기
fun getScheduleTimeFormat(dateTime: LocalDateTime): String {
    val ampm = if (dateTime.get(ChronoField.AMPM_OF_DAY) == 0) "오전" else "오후"
    val hour = "${dateTime.get(ChronoField.CLOCK_HOUR_OF_AMPM)}시"
    val minute = if (dateTime.minute == 0) "" else "${dateTime.minute}분"

    return "$ampm $hour $minute"
}

@Preview(showBackground = true, heightDp = 400)
@Composable
fun CalendarEmptySchedulePreview() {
    CalendarSchedule(
        LocalDate.now(),
        emptyList(),
        {}
    )
}

@Preview(showBackground = true, heightDp = 400)
@Composable
fun CalendarSchedulePreview() {
    CalendarSchedule(
        LocalDate.now(),
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
        ),
        {}
    )
}