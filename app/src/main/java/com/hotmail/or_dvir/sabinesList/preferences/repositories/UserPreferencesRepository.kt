package com.hotmail.or_dvir.sabinesList.preferences.repositories

import com.hotmail.or_dvir.sabinesList.preferences.ThemePreference
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun getThemeMode(): Flow<ThemePreference>
    suspend fun setThemeMode(mode: ThemePreference)
}
