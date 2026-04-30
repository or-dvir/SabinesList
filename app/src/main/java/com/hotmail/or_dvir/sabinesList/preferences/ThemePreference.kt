package com.hotmail.or_dvir.sabinesList.preferences

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ThemePreference {
    @SerialName("light")
    LIGHT,
    @SerialName("system")
    SYSTEM,
    @SerialName("dark")
    DARK
}