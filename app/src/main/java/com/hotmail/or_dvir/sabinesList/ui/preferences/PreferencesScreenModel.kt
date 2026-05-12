package com.hotmail.or_dvir.sabinesList.ui.preferences

import cafe.adriel.voyager.core.model.screenModelScope
import com.hotmail.or_dvir.sabinesList.preferences.ThemePreference
import com.hotmail.or_dvir.sabinesList.preferences.repositories.UserPreferencesRepository
import com.hotmail.or_dvir.sabinesList.ui.BaseScreenModel
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class PreferencesScreenModel @Inject constructor(
    private val repo: UserPreferencesRepository
) : BaseScreenModel() {
    internal val userSelectedTheme = repo.getThemeMode()

    internal fun onPreferencesEvent(event: PreferencesEvent) {
        when (event) {
            is PreferencesEvent.SetTheme -> setTheme(event.theme)
        }
    }

    private fun setTheme(theme: ThemePreference) =
        screenModelScope.launch { repo.setThemeMode(theme) }

    internal sealed class PreferencesEvent {
        data class SetTheme(val theme: ThemePreference) : PreferencesEvent()
    }
}
