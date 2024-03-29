package com.hotmail.or_dvir.sabinesList.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val colorPrimaryVariant = Color(0xff009867)
private val colorSecondary = Color(0xffB43D6B)
private val colorStatusBarDark = Color(0xff0e0e0e)

private val lightColorPalette = lightColors(
    primary = Color(0xff3db486),
    primaryVariant = colorPrimaryVariant,
    secondary = colorSecondary,
    secondaryVariant = Color(0xff773256)
)

private val darkColorPalette = darkColors(
    primary = Color(0xff93d2b6),
    primaryVariant = colorPrimaryVariant,
    secondary = colorSecondary,
    secondaryVariant = colorSecondary
)

@Composable
fun SabinesListTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        darkColorPalette
    } else {
        lightColorPalette
    }

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = if (darkTheme) colorStatusBarDark else colorPrimaryVariant
        )
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}