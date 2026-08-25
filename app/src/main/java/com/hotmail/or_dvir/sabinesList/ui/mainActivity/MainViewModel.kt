package com.hotmail.or_dvir.sabinesList.ui.mainActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.hotmail.or_dvir.sabinesList.auth.repositories.AuthRepository
import com.hotmail.or_dvir.sabinesList.preferences.repositories.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    preferencesRepo: UserPreferencesRepository,
    authRepo: AuthRepository
) : ViewModel() {
    val themePreference = preferencesRepo.getThemeMode()

    val authState: StateFlow<FirebaseUser?> = authRepo.authState

    val isUserLoggedIn: StateFlow<Boolean> = authRepo.authState
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val isEmailVerified: StateFlow<Boolean> = authRepo.authState
        .map { it?.isEmailVerified == true }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
}
