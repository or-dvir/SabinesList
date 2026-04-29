package com.hotmail.or_dvir.sabinesList.ui.preferences

import androidx.lifecycle.ViewModel
import com.hotmail.or_dvir.sabinesList.preferences.repositories.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    userPreferencesRepo: UserPreferencesRepository
) : ViewModel() {
    val userSelectedTheme = userPreferencesRepo.getThemeMode()
}