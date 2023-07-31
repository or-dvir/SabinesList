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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.hotmail.or_dvir.sabinesList.ui.collectIsDarkMode
import com.hotmail.or_dvir.sabinesList.ui.theme.BottomNavigationColors
import com.hotmail.or_dvir.sabinesList.ui.theme.LocalBottomNavigationColors
import com.hotmail.or_dvir.sabinesList.ui.theme.SabinesListTheme
import com.hotmail.or_dvir.sabinesList.ui.userLists.UserListsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // todo there is a delay until isDarkMode is loaded, and the screen
            //  is "light theme" until then. i need a splash screen!!!!
            SabinesListTheme(viewModel.collectIsDarkMode()) {
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
                        Navigator(UserListsScreen()) {
                            SlideTransition(it)
                        }
                    }
                }
            }
        }
    }
}
