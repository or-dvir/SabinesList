package com.hotmail.or_dvir.sabinesList.preferences

import androidx.annotation.StringRes
import com.hotmail.or_dvir.sabinesList.R
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class ThemePreference(@param: StringRes val labelRes: Int) {
    @SerialName("light")
    LIGHT(R.string.preferenceScreen_theme_light),

    @SerialName("system")
    SYSTEM(R.string.preferenceScreen_theme_system),

    @SerialName("dark")
    DARK(R.string.preferenceScreen_theme_dark);

    companion object {
        val Default = SYSTEM
    }
}