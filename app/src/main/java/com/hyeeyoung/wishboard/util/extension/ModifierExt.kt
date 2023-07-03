package com.hyeeyoung.wishboard.util.extension

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import com.hyeeyoung.wishboard.presentation.theme.BlackAlpha5

fun Modifier.coloredForeground() = this.drawWithContent {
    drawContent()
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            this.color = BlackAlpha5
        }
        canvas.drawRect(
            Rect(
                offset = Offset.Zero,
                size = Size(size.width, size.height)
            ),
            paint
        )
    }
}
