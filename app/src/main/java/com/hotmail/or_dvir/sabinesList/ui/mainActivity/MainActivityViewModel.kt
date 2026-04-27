package com.hotmail.or_dvir.sabinesList.ui.mainActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotmail.or_dvir.sabinesList.preferences.repositories.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userPreferencesRepo: UserPreferencesRepository,
) : ViewModel() {
    val userSelectedTheme = userPreferencesRepo.getThemeMode()
}
