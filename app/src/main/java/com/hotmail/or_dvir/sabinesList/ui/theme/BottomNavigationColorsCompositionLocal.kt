package com.hotmail.or_dvir.sabinesList.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class BottomNavigationColors(
    val unselected: Color = Color.White.copy(alpha = 0.5f),
    val selected: Color = Color.White
)

val LocalBottomNavigationColors = compositionLocalOf { BottomNavigationColors() }
