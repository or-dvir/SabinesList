package com.hotmail.or_dvir.sabinesList.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.ContentAlpha
import androidx.compose.ui.graphics.Color

val Colors.menuIconColor: Color
    get() = Color.White

val Colors.bottomNavigationSelectedColor: Color
    get() = Color.White

val Colors.bottomNavigationUnSelectedColor: Color
    get() = bottomNavigationSelectedColor.copy(alpha = 0.5f)

val Colors.fabContentColor: Color
    get() = Color.White
