package com.hotmail.or_dvir.sabinesList.ui.mainActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.hotmail.or_dvir.sabinesList.ui.collectIsDarkMode
import com.hotmail.or_dvir.sabinesList.ui.userLists.UserListsScreen
import com.hotmail.or_dvir.sabinesList.ui.theme.SabinesListTheme
import dagger.hilt.android.AndroidEntryPoint

//todo
//  update launcher icon
//      update credits for launcher icon in app
//      update credits for launcher icon in readme file
//  update app colors



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
                    Navigator(UserListsScreen()) {
                        SlideTransition(it)
                    }
                }
            }
        }
    }
}
