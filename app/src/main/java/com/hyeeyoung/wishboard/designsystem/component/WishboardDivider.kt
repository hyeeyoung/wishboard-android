package com.hyeeyoung.wishboard.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R

@Composable
fun WishboardDivider() = Divider(
    modifier = Modifier.fillMaxWidth(),
    thickness = 1.dp,
    color = colorResource(id = R.color.gray_100)
)
