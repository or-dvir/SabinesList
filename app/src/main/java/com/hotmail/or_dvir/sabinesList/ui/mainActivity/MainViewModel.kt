package com.hotmail.or_dvir.sabinesList.ui.mainActivity

import androidx.lifecycle.ViewModel
import com.hotmail.or_dvir.sabinesList.preferences.repositories.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    repo: UserPreferencesRepository
) : ViewModel() {
    val themePreference = repo.getThemeMode()
}
