package com.hyeeyoung.wishboard.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.designsystem.theme.Gray700
import com.hyeeyoung.wishboard.designsystem.theme.WishBoardTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WishboardSnackbarHost(hostState: SnackbarHostState) =
    SnackbarHost(hostState = hostState) { data ->
        WishboardSnackbar(message = data.visuals.message)
    }

@Composable
fun WishboardSnackbar(message: String) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .background(Gray700, RoundedCornerShape(45.dp))
                .padding(vertical = 16.dp, horizontal = 32.dp),
            text = message,
            color = Color.White,
            textAlign = TextAlign.Center,
            style = WishBoardTheme.typography.suitD2M
        )
        Spacer(modifier = Modifier.size(32.dp))
    }
}

fun SnackbarHostState.showSnackbar(message: String, coroutineScope: CoroutineScope) = coroutineScope.launch {
    val job = coroutineScope.launch {
        showSnackbar(message)
    }
    delay(2000)
    job.cancel()
}

@Preview(showSystemUi = true)
@Composable
fun WishboardSnackbarPreview() {
    WishboardSnackbar("네트워크 연결 상태를 확인해 주세요.")
}