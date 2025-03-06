package com.example.cookup.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.cookup.R

val fontFamily = FontFamily(
    Font(R.font.noto_sans_hebrew)
)

val Typography = Typography(
    body1 = TextStyle(
        fontFamily = fontFamily,
        fontSize = 16.sp
    )
)
