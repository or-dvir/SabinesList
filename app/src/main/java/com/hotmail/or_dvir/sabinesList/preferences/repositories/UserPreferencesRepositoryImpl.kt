package com.hotmail.or_dvir.sabinesList.preferences.repositories

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.hotmail.or_dvir.sabinesList.database.repositories.shouldNotBeCancelled
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

private const val USER_PREFERENCES_NAME = "UserPreferences"
private val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val scopeThatShouldNotBeCancelled: CoroutineScope,
    private val dispatcher: CoroutineDispatcher
) : UserPreferencesRepository {

    private companion object {
        const val KEY_NAME_IS_DARK_MODE = "isDarkMode"
        val key_isDarkMode = booleanPreferencesKey(KEY_NAME_IS_DARK_MODE)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun isDarkMode() = context.dataStore.data.mapLatest { it[key_isDarkMode] ?: false }

    override suspend fun setDarkMode(darkMode: Boolean) {
        shouldNotBeCancelled(
            dispatcher = dispatcher,
            scopeThatShouldNotBeCancelled = scopeThatShouldNotBeCancelled
        ) {
            context.dataStore.edit {
                it[key_isDarkMode] = darkMode
            }
        }
    }
}
