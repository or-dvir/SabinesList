package com.hotmail.or_dvir.sabinesList.ui.preferences

import app.cash.turbine.test
import com.hotmail.or_dvir.sabinesList.MainDispatcherRule
import com.hotmail.or_dvir.sabinesList.preferences.ThemePreference
import com.hotmail.or_dvir.sabinesList.preferences.repositories.UserPreferencesRepository
import com.hotmail.or_dvir.sabinesList.ui.preferences.PreferencesScreenModel.PreferencesEvent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class PreferencesScreenModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repo = mockk<UserPreferencesRepository>()
    private lateinit var screenModel: PreferencesScreenModel

    private val themeFlow = MutableStateFlow(ThemePreference.Default)

    @Before
    fun setup() {
        every { repo.getThemeMode() } returns themeFlow
        screenModel = PreferencesScreenModel(repo)
    }

    @Test
    fun `userSelectedTheme emits correctly`() = runTest {
        screenModel.userSelectedTheme.test {
            assertEquals(ThemePreference.Default, awaitItem())
            themeFlow.value = ThemePreference.DARK
            assertEquals(ThemePreference.DARK, awaitItem())
        }
    }

    @Test
    fun `onSetTheme calls repo`() = runTest {
        val theme = ThemePreference.LIGHT
        coEvery { repo.setThemeMode(theme) } returns Unit

        screenModel.onPreferencesEvent(PreferencesEvent.SetTheme(theme))
        coVerify { repo.setThemeMode(theme) }
    }
}
