package com.hotmail.or_dvir.sabinesList.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

//  for both light and dark theme


//todo make sure the color of buttons is contrasted enough (e.g. bottom navigation)
private val colorPrimaryVariant = Color(0xff009867)
private val colorSecondary = Color(0xffB43D6B)
private val colorStatusBarDark = Color(0xff0e0e0e)

private val LightColorPalette = lightColors(
    primary = Color(0xff3db486),
    primaryVariant = colorPrimaryVariant,
    secondary = colorSecondary,
    secondaryVariant = Color(0xff773256)
)

private val DarkColorPalette = darkColors(
    primary = Color(0xff93d2b6),
    primaryVariant = colorPrimaryVariant,
    secondary = colorSecondary,
    secondaryVariant = colorSecondary
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