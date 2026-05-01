package com.hotmail.or_dvir.sabinesList.preferences.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import app.cash.turbine.test
import com.hotmail.or_dvir.sabinesList.preferences.ThemePreference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class UserPreferencesRepositoryImplTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private lateinit var repository: UserPreferencesRepositoryImpl
    private lateinit var dataStore: DataStore<Preferences>
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher + Job())

    @Before
    fun setup() {
        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { tmpFolder.newFile("test.preferences_pb") }
        )
        repository = UserPreferencesRepositoryImpl(
            dataStore = dataStore,
            scopeThatShouldNotBeCancelled = testScope,
            dispatcher = testDispatcher
        )
    }

    @Test
    fun `getThemeMode returns Default when no value is saved`() = runTest(testDispatcher) {
        repository.getThemeMode().test {
            assertEquals(ThemePreference.Default, awaitItem())
        }
    }

    @Test
    fun `setThemeMode updates the theme`() = runTest(testDispatcher) {
        repository.setThemeMode(ThemePreference.DARK)

        repository.getThemeMode().test {
            // Might have the default first if collection starts before write is noticed,
            // but setThemeMode is a suspend function so it should be finished.
            assertEquals(ThemePreference.DARK, awaitItem())
        }
    }

    @Test
    fun `migration - returns DARK when old isDarkMode is true`() = runTest(testDispatcher) {
        val oldKey = booleanPreferencesKey("isDarkMode")
        dataStore.edit { it[oldKey] = true }

        repository.getThemeMode().test {
            assertEquals(ThemePreference.DARK, awaitItem())
        }
    }

    @Test
    fun `migration - returns LIGHT when old isDarkMode is false`() = runTest(testDispatcher) {
        val oldKey = booleanPreferencesKey("isDarkMode")
        dataStore.edit { it[oldKey] = false }

        repository.getThemeMode().test {
            assertEquals(ThemePreference.LIGHT, awaitItem())
        }
    }
}
