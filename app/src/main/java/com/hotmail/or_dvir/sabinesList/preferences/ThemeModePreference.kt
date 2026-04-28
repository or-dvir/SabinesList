package com.hotmail.or_dvir.sabinesList.preferences

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ThemeModePreference {
    @SerialName("light")
    LIGHT,
    @SerialName("dark")
    DARK,
    @SerialName("system")
    SYSTEM
}