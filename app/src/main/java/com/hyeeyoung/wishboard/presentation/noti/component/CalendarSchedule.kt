package com.hyeeyoung.wishboard.presentation.noti.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.domain.model.NotiItemInfo
import com.hyeeyoung.wishboard.presentation.common.component.ColoredImage
import com.hyeeyoung.wishboard.presentation.noti.types.NotiType
import com.hyeeyoung.wishboard.presentation.theme.Gray300
import com.hyeeyoung.wishboard.presentation.theme.Gray50
import com.hyeeyoung.wishboard.presentation.theme.Gray700
import com.hyeeyoung.wishboard.presentation.theme.WishBoardTheme
import java.time.LocalDate

@Composable
fun CalendarSchedule(selectedDate: LocalDate, notiItems: List<NotiItemInfo>) {
    Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp)) {
        Text(
            text = stringResource(
                id = R.string.noti_calendar_schedule_title,
                selectedDate.monthValue, selectedDate.dayOfMonth
            ),
            color = Gray700,
            style = WishBoardTheme.typography.suitH4
        )
        Spacer(modifier = Modifier.size(16.dp)) // TODO dimensionResource 사용
        LazyColumn(
            contentPadding = PaddingValues(bottom = 64.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(notiItems) { noti ->
                ScheduleItem(noti = noti)
            }
        }
    }
}

@Composable
fun ScheduleItem(noti: NotiItemInfo) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
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
            text = noti.notiDate,
            color = Gray300,
            style = WishBoardTheme.typography.suitD3
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarSchedulePreview() {
    CalendarSchedule(
        LocalDate.now(),
        listOf(
            NotiItemInfo(
                1,
                "https://image.msscdn.net/images/goods_img/20220222/2377269/2377269_16777177260753_500.jpg",
                "W CLASSIC LOGO TEE white",
                "https://www.musinsa.com/app/goods/2377269",
                0,
                NotiType.RESTOCK,
                "오전 10시"
            ),
            NotiItemInfo(
                2,
                "https://image.msscdn.net/images/goods_img/20230427/3267246/3267246_16825933559850_500.jpg",
                "체리 자카드 패턴 숏 슬리브 가디건 [핑크]",
                "https://www.musinsa.com/app/goods/3267246/0",
                0,
                NotiType.PREORDER,
                "오후 1시"
            )
        )
    )
}