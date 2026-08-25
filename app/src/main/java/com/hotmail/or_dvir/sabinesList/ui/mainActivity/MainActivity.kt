package com.hotmail.or_dvir.sabinesList.ui.mainActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.hotmail.or_dvir.sabinesList.collectAsStateLifecycleAware
import com.hotmail.or_dvir.sabinesList.preferences.ThemePreference
import com.hotmail.or_dvir.sabinesList.preferences.ThemePreference.DARK
import com.hotmail.or_dvir.sabinesList.preferences.ThemePreference.LIGHT
import com.hotmail.or_dvir.sabinesList.preferences.ThemePreference.SYSTEM
import com.hotmail.or_dvir.sabinesList.ui.auth.AuthScreen
import com.hotmail.or_dvir.sabinesList.ui.auth.VerifyEmailScreen
import com.hotmail.or_dvir.sabinesList.ui.theme.BottomNavigationColors
import com.hotmail.or_dvir.sabinesList.ui.theme.LocalBottomNavigationColors
import com.hotmail.or_dvir.sabinesList.ui.theme.SabinesListTheme
import com.hotmail.or_dvir.sabinesList.ui.userListsScreen.UserListsScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        var themePreference: ThemePreference by mutableStateOf(ThemePreference.Default)
        var isThemePreferenceLoaded by mutableStateOf(value = false)
        var startScreen: Screen? by mutableStateOf(null)

        installSplashScreen().apply {
            setKeepOnScreenCondition { !isThemePreferenceLoaded || startScreen == null }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.themePreference.collectLatest {
                    themePreference = it
                    isThemePreferenceLoaded = true
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Determine the initial screen. 
                // Note: This logic only runs once to set the initial destination.
                // Reacting to login/logout changes happens inside the setContent block.
                val isLoggedIn = viewModel.isUserLoggedIn.value
                val isVerified = viewModel.isEmailVerified.value

                startScreen = when {
                    !isLoggedIn -> AuthScreen()
                    !isVerified -> VerifyEmailScreen()
                    else -> UserListsScreen()
                }
            }
        }

        super.onCreate(savedInstanceState)

        setContent {
            SabinesListTheme(
                darkTheme = when (themePreference) {
                    LIGHT -> false
                    DARK -> true
                    SYSTEM -> isSystemInDarkTheme()
                }, 
            ) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val bottomNavColors = if (isSystemInDarkTheme()) {
                        BottomNavigationColors(unselected = Color(0x73f0f0f0))
                    } else {
                        BottomNavigationColors(unselected = Color(0xbff0f0f0))
                    }

                    CompositionLocalProvider(LocalBottomNavigationColors provides bottomNavColors) {
                        startScreen?.let { initialScreen ->
                            Navigator(initialScreen) { navigator ->
                                val isLoggedIn by viewModel.isUserLoggedIn.collectAsStateLifecycleAware(false)
                                val isVerified by viewModel.isEmailVerified.collectAsStateLifecycleAware(false)

                                LaunchedEffect(isLoggedIn, isVerified) {
                                    val currentScreen = navigator.lastItem
                                    when {
                                        !isLoggedIn && currentScreen !is AuthScreen -> {
                                            navigator.replaceAll(AuthScreen())
                                        }

                                        isLoggedIn && !isVerified && currentScreen !is VerifyEmailScreen -> {
                                            navigator.replaceAll(VerifyEmailScreen())
                                        }

                                        isLoggedIn && isVerified && currentScreen !is UserListsScreen -> {
                                            navigator.replaceAll(UserListsScreen())
                                        }
                                    }
                                }

                                SlideTransition(navigator)
                            }
                        }
                    }
                }
            }
        }
    }
}
