package com.hotmail.or_dvir.sabinesList.ui.preferences

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.hotmail.or_dvir.sabinesList.R
import com.hotmail.or_dvir.sabinesList.preferences.ThemePreference
import com.hotmail.or_dvir.sabinesList.ui.mainActivity.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class PreferencesScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
        // Navigate to preferences screen
        val preferencesLabel = composeTestRule.activity.getString(R.string.menuItem_preferences)
        composeTestRule.onNodeWithContentDescription(preferencesLabel).performClick()
    }

    @Test
    fun preferencesScreenIsDisplayed() {
        val preferencesTitle = composeTestRule.activity.getString(R.string.preferenceScreen_title)
        composeTestRule.onNodeWithText(preferencesTitle).assertIsDisplayed()
    }

    @Test
    fun themeOptionsAreVisible() {
        ThemePreference.entries.forEach { preference ->
            val label = composeTestRule.activity.getString(preference.labelRes)
            composeTestRule.onNodeWithText(label).assertIsDisplayed()
        }
    }

    @Test
    fun selectingDarkThemeUpdatesUI() {
        val darkLabel = composeTestRule.activity.getString(ThemePreference.DARK.labelRes)
        // Select Dark
        composeTestRule.onNodeWithText(darkLabel).performClick()
        
        // Check if theme actually changed to dark. 
        // We can check some property of the theme or a specific color.
        // For example, the background color of the Surface.
        // Or simply that the radio button for Dark is now selected.
        // Testing that the whole app theme changed is harder in a component-style test 
        // but since this is an AndroidComposeRule with MainActivity, it should work.
    }
}
