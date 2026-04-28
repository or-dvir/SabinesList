package com.hotmail.or_dvir.sabinesList.preferences.repositories

import com.hotmail.or_dvir.sabinesList.preferences.ThemeModePreference
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun getThemeMode(): Flow<ThemeModePreference>
    suspend fun setThemeMode(mode: ThemeModePreference)
}
