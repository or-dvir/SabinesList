package com.hotmail.or_dvir.sabinesList.ui.listItemsScreen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import com.hotmail.or_dvir.sabinesList.R
import com.hotmail.or_dvir.sabinesList.ui.mainActivity.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ListItemsScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
        
        // Add a list and navigate to it
        val addUserListLabel = composeTestRule.activity.getString(R.string.contentDescription_addUserList)
        composeTestRule.onNodeWithContentDescription(addUserListLabel).performClick()
        
        val listName = "Test List"
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.hint_listName)).performTextInput(listName)
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.create)).performClick()
        
        composeTestRule.onNodeWithText(listName).performClick()
    }

    @Test
    fun uncheckAllConfirmationDialogAppears() {
        // Add an item first
        val addItemLabel = composeTestRule.activity.getString(R.string.contentDescription_addListItem)
        composeTestRule.onNodeWithContentDescription(addItemLabel).performClick()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.hint_itemName)).performTextInput("Item to Uncheck")
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.create)).performClick()

        // Open overflow menu for "Uncheck All"
        val moreActionsLabel = composeTestRule.activity.getString(R.string.contentDescription_moreActions)
        composeTestRule.onNodeWithContentDescription(moreActionsLabel).performClick()
        
        val uncheckAllLabel = composeTestRule.activity.getString(R.string.menuItem_uncheckAll)
        composeTestRule.onNodeWithText(uncheckAllLabel).performClick()

        // Verify confirmation dialog
        val confirmationMessage = composeTestRule.activity.getString(R.string.listItemsScreen_uncheckAllConfirmation).trim()
        composeTestRule.onNodeWithText(confirmationMessage).assertIsDisplayed()
    }

    @Test
    fun navigatesToCorrectList() {
        // Verify title matches list name
        composeTestRule.onNodeWithText("Test List").assertIsDisplayed()
    }

    @Test
    fun dynamicFiltering_uncheckedItemDisappearsFromCheckedFilter() {
        // Add a checked item
        val addItemLabel = composeTestRule.activity.getString(R.string.contentDescription_addListItem)
        composeTestRule.onNodeWithContentDescription(addItemLabel).performClick()
        
        val itemName = "To Be Unchecked"
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.hint_itemName)).performTextInput(itemName)
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.create)).performClick()

        // Check it (initially unchecked)
        val uncheckedDescription = composeTestRule.activity.getString(R.string.contentDescription_checkbox_unchecked, itemName)
        composeTestRule.onNodeWithContentDescription(uncheckedDescription).performClick()
        
        // Go to "Checked" filter
        val checkedFilterLabel = composeTestRule.activity.getString(R.string.bottomNavigation_checked)
        composeTestRule.onNodeWithText(checkedFilterLabel).performClick()
        
        // Item SHOULD be there (now checked)
        val checkedDescription = composeTestRule.activity.getString(R.string.contentDescription_checkbox_checked, itemName)
        composeTestRule.onNodeWithText(itemName).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(checkedDescription).assertIsDisplayed()
        
        // Uncheck it
        composeTestRule.onNodeWithContentDescription(checkedDescription).performClick()
        
        // Item should DISAPPEAR immediately from this view
        composeTestRule.onNodeWithText(itemName).assertDoesNotExist()
    }
}
