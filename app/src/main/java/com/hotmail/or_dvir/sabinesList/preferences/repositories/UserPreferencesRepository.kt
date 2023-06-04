package com.hotmail.or_dvir.sabinesList.preferences.repositories

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun isDarkMode(): Flow<Boolean>
    suspend fun setDarkMode(darkMode: Boolean)
}
