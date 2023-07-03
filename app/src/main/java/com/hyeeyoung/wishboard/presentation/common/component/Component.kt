package com.hyeeyoung.wishboard.presentation.common.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.util.extension.coloredForeground


@Composable
fun WishboardDivider() = Divider(
    modifier = Modifier.fillMaxWidth(),
    thickness = 1.dp,
    color = colorResource(id = R.color.gray_100)
)

/** 전경색을 입힌 이미지를 로드 */
@Composable
fun ColoredImage(
    model: Any?,
    modifier: Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null
) {
    Surface(modifier = modifier) {
        AsyncImage(
            modifier = Modifier
                .coloredForeground(),
            model = model,
            contentScale = contentScale,
            contentDescription = contentDescription
        )
    }
}
