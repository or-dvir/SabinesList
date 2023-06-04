package com.hotmail.or_dvir.sabinesList.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val secondaryColor = Color(0xffff9800)
private val secondaryVariantColor = Color(0xfff57d00)
private val primaryVariantColor = Color(0xff7900CC)
private val statusBarDark = Color(0xff101010)

private val DarkColorPalette = darkColors(
    primary = Color(0xffd094ff),
    primaryVariant = primaryVariantColor,
    secondary = secondaryColor,
    secondaryVariant = secondaryVariantColor
)

private val LightColorPalette = lightColors(
    primary = Color(0xff9800FF),
    primaryVariant = primaryVariantColor,
    secondary = secondaryColor,
    secondaryVariant = secondaryVariantColor
)

@Composable
fun SabinesListTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = if (darkTheme) statusBarDark else primaryVariantColor
        )
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}