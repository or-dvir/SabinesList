package com.hotmail.or_dvir.sabinesList.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController


stopped here.
adjust new colors!!!
primary: 0xff3db486
secondary: 0xffB43D6B

private val secondaryColor = Color(0xffB43D6B)
private val secondaryVariantColor = Color(0xfff57d00)
//private val secondaryVariantColor = Color(0xfff57d00)
//private val primaryVariantColor = Color(0xff7900CC)
private val statusBarDark = Color(0xff101010)


val newPrimary = Color(0xff3db486)
//val newPrimary = Color(0xff4DD0E1)
private val primaryVariantColor = newPrimary

private val LightColorPalette = lightColors(
    primary = newPrimary,
//    primary = Color(0xff42A5F5),
    primaryVariant = primaryVariantColor,
    secondary = secondaryColor,
    secondaryVariant = secondaryVariantColor
)

private val DarkColorPalette = darkColors(
    primary = Color(0xffd094ff),
//    primary = Color(0xffd094ff),
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