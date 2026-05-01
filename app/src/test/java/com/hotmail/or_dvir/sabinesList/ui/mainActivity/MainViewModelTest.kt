package com.hotmail.or_dvir.sabinesList.ui.mainActivity

import app.cash.turbine.test
import com.hotmail.or_dvir.sabinesList.MainDispatcherRule
import com.hotmail.or_dvir.sabinesList.preferences.ThemePreference
import com.hotmail.or_dvir.sabinesList.preferences.repositories.UserPreferencesRepository
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
class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repo = mockk<UserPreferencesRepository>()
    private lateinit var viewModel: MainViewModel

    private val themeFlow = MutableStateFlow(ThemePreference.Default)

    @Before
    fun setup() {
        every { repo.getThemeMode() } returns themeFlow
        viewModel = MainViewModel(repo)
    }

    @Test
    fun `themePreference emits correctly`() = runTest {
        viewModel.themePreference.test {
            assertEquals(ThemePreference.Default, awaitItem())
            themeFlow.value = ThemePreference.DARK
            assertEquals(ThemePreference.DARK, awaitItem())
        }
    }
}
