package com.hotmail.or_dvir.sabinesList.preferences.repositories

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hotmail.or_dvir.sabinesList.database.repositories.shouldNotBeCancelled
import com.hotmail.or_dvir.sabinesList.preferences.ThemePreference
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import kotlinx.serialization.json.Json

private const val USER_PREFERENCES_NAME = "UserPreferences"
private val Context.dataStore by preferencesDataStore(name = USER_PREFERENCES_NAME)

class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val scopeThatShouldNotBeCancelled: CoroutineScope,
    private val dispatcher: CoroutineDispatcher
) : UserPreferencesRepository {

    private companion object {
        // todo OLD. kept for migration purposes. remove mid-late June 2026.
        const val KEY_NAME_IS_DARK_MODE = "isDarkMode"
        val key_isDarkMode = booleanPreferencesKey(KEY_NAME_IS_DARK_MODE)

        // NEW
        const val KEY_NAME_THEME_MODE_PREFERENCE = "themeModePreference"
        val key_themeMode = stringPreferencesKey(KEY_NAME_THEME_MODE_PREFERENCE)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getThemeMode() = context.dataStore.data.mapLatest { prefs ->
        val fallback = ThemePreference.SYSTEM
        val oldPreferenceIsDark = prefs[key_isDarkMode]

        // Check for old boolean preference
        if (oldPreferenceIsDark != null) {
            return@mapLatest if (oldPreferenceIsDark) {
                ThemePreference.DARK
            } else {
                ThemePreference.LIGHT
            }
        }

        // No old preference, check for new enum values
        val savedValue = prefs[key_themeMode] ?: return@mapLatest fallback

        // Try to decode the saved enum
        try {
            Json.decodeFromString<ThemePreference>(savedValue)
        } catch (e: Exception) {
            Log.e(
                UserPreferencesRepositoryImpl::class.java.simpleName,
                "Error decoding theme",
                e
            )

            fallback
        }
    }

    override suspend fun setThemeMode(mode: ThemePreference) {        shouldNotBeCancelled(
            dispatcher = dispatcher,
            scopeThatShouldNotBeCancelled = scopeThatShouldNotBeCancelled
        ) {
            context.dataStore.edit {
                it[key_themeMode] = Json.encodeToString(mode)            }
        }
    }
}
