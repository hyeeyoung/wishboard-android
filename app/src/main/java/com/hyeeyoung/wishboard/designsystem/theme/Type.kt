package com.hyeeyoung.wishboard.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.hyeeyoung.wishboard.R

val SuitFamily = FontFamily(
    Font(R.font.suit_eb, FontWeight.ExtraBold, FontStyle.Normal),
    Font(R.font.suit_b, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.suit_sb, FontWeight.SemiBold, FontStyle.Normal),
    Font(R.font.suit_m, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.suit_r, FontWeight.Normal, FontStyle.Normal),
)

val MontserratFamily = FontFamily(
    Font(R.font.montserrat_b, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.montserrat_sb, FontWeight.SemiBold, FontStyle.Normal),
    Font(R.font.montserrat_m, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.montserrat_r, FontWeight.Normal, FontStyle.Normal),
)

@Immutable
data class WishBoardTypography(
    val suitH1: TextStyle = TextStyle(
        fontFamily = SuitFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    val suitH2: TextStyle = TextStyle(
        fontFamily = SuitFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    val suitH3: TextStyle = TextStyle(
        fontFamily = SuitFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    val suitH4: TextStyle = TextStyle(
        fontFamily = SuitFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    val suitH5: TextStyle = TextStyle(
        fontFamily = SuitFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    ),
    val suitH6: TextStyle = TextStyle(
        fontFamily = SuitFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp
    ),
    val suitB1: TextStyle = TextStyle(
        fontFamily = SuitFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    val suitB2: TextStyle = TextStyle(
        fontFamily = SuitFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),
    val suitB3: TextStyle = TextStyle(
        fontFamily = SuitFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    val suitB4: TextStyle = TextStyle(
        fontFamily = SuitFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp
    ),
    val suitB5: TextStyle = TextStyle(
        fontFamily = SuitFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    val suitD1: TextStyle = TextStyle(
        fontFamily = SuitFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    val suitD2: TextStyle = TextStyle(
        fontFamily = SuitFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    val suitD3: TextStyle = TextStyle(
        fontFamily = SuitFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    val montserratH1: TextStyle = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    val montserratH2: TextStyle = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    val montserratH3: TextStyle = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    val montserratB1: TextStyle = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),
    val montserratB2: TextStyle = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    val montserratD1: TextStyle = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 9.sp
    ),
    val suitB3M: TextStyle = suitB3.copy(lineHeight = 20.sp),
)

val LocalTypography = staticCompositionLocalOf { WishBoardTypography() }