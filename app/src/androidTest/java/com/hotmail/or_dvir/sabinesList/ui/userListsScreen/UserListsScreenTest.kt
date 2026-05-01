package com.hotmail.or_dvir.sabinesList.ui.userListsScreen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
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
class UserListsScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
        @Test
    fun deleteConfirmationDialogAppears() {
        val addUserListLabel = composeTestRule.activity.getString(R.string.contentDescription_addUserList)
        composeTestRule.onNodeWithContentDescription(addUserListLabel).performClick()
        
        val listName = "To Delete"
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.hint_listName)).performTextInput(listName)
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.create)).performClick()

        // Trigger delete via swipe
        // Note: SwipeToDeleteOrEdit uses DismissDirection.StartToEnd for delete (Red background)
        composeTestRule.onNodeWithText(listName).performTouchInput {
            swipeRight()
        }

        // Verify dialog appears
        val confirmationMessage = composeTestRule.activity.getString(R.string.homeScreen_deleteConfirmation)
        composeTestRule.onNodeWithText(confirmationMessage).assertIsDisplayed()
    }
}

    @Test
    fun emptyStateIsVisibleWhenNoListsExist() {
        val emptyText = composeTestRule.activity.getString(R.string.homeScreen_emptyView)
        composeTestRule.onNodeWithText(emptyText).assertIsDisplayed()
        @Test
    fun deleteConfirmationDialogAppears() {
        val addUserListLabel = composeTestRule.activity.getString(R.string.contentDescription_addUserList)
        composeTestRule.onNodeWithContentDescription(addUserListLabel).performClick()
        
        val listName = "To Delete"
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.hint_listName)).performTextInput(listName)
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.create)).performClick()

        // Trigger delete via swipe
        // Note: SwipeToDeleteOrEdit uses DismissDirection.StartToEnd for delete (Red background)
        composeTestRule.onNodeWithText(listName).performTouchInput {
            swipeRight()
        }

        // Verify dialog appears
        val confirmationMessage = composeTestRule.activity.getString(R.string.homeScreen_deleteConfirmation)
        composeTestRule.onNodeWithText(confirmationMessage).assertIsDisplayed()
    }
}

    @Test
    fun searchIconIsHiddenWhenNoListsExist() {
        val searchLabel = composeTestRule.activity.getString(R.string.menuItem_search)
        composeTestRule.onNodeWithContentDescription(searchLabel).assertDoesNotExist()
        @Test
    fun deleteConfirmationDialogAppears() {
        val addUserListLabel = composeTestRule.activity.getString(R.string.contentDescription_addUserList)
        composeTestRule.onNodeWithContentDescription(addUserListLabel).performClick()
        
        val listName = "To Delete"
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.hint_listName)).performTextInput(listName)
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.create)).performClick()

        // Trigger delete via swipe
        // Note: SwipeToDeleteOrEdit uses DismissDirection.StartToEnd for delete (Red background)
        composeTestRule.onNodeWithText(listName).performTouchInput {
            swipeRight()
        }

        // Verify dialog appears
        val confirmationMessage = composeTestRule.activity.getString(R.string.homeScreen_deleteConfirmation)
        composeTestRule.onNodeWithText(confirmationMessage).assertIsDisplayed()
    }
}

    @Test
    fun clickingPreferencesNavigatesToPreferencesScreen() {
        val preferencesLabel = composeTestRule.activity.getString(R.string.menuItem_preferences)
        composeTestRule.onNodeWithContentDescription(preferencesLabel).performClick()

        val preferencesTitle = composeTestRule.activity.getString(R.string.preferenceScreen_title)
        composeTestRule.onNodeWithText(preferencesTitle).assertIsDisplayed()
        @Test
    fun deleteConfirmationDialogAppears() {
        val addUserListLabel = composeTestRule.activity.getString(R.string.contentDescription_addUserList)
        composeTestRule.onNodeWithContentDescription(addUserListLabel).performClick()
        
        val listName = "To Delete"
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.hint_listName)).performTextInput(listName)
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.create)).performClick()

        // Trigger delete via swipe
        // Note: SwipeToDeleteOrEdit uses DismissDirection.StartToEnd for delete (Red background)
        composeTestRule.onNodeWithText(listName).performTouchInput {
            swipeRight()
        }

        // Verify dialog appears
        val confirmationMessage = composeTestRule.activity.getString(R.string.homeScreen_deleteConfirmation)
        composeTestRule.onNodeWithText(confirmationMessage).assertIsDisplayed()
    }
}

    @Test
    fun closingSearchModeReturnsToNormalView() {
        val addUserListLabel = composeTestRule.activity.getString(R.string.contentDescription_addUserList)
        composeTestRule.onNodeWithContentDescription(addUserListLabel).performClick()
        
        val listName = "Test List"
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.hint_listName)).performTextInput(listName)
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.create)).performClick()

        val searchLabel = composeTestRule.activity.getString(R.string.menuItem_search)
        composeTestRule.onNodeWithContentDescription(searchLabel).performClick()
        
        val searchHint = composeTestRule.activity.getString(R.string.search)
        composeTestRule.onNodeWithText(searchHint).assertIsDisplayed()

        val exitSearchLabel = composeTestRule.activity.getString(R.string.contentDescription_exitSearch)
        composeTestRule.onNodeWithContentDescription(exitSearchLabel).performClick()

        composeTestRule.onNodeWithText(searchHint).assertDoesNotExist()
        val title = composeTestRule.activity.getString(R.string.homeScreen_title)
        composeTestRule.onNodeWithText(title).assertIsDisplayed()
        @Test
    fun deleteConfirmationDialogAppears() {
        val addUserListLabel = composeTestRule.activity.getString(R.string.contentDescription_addUserList)
        composeTestRule.onNodeWithContentDescription(addUserListLabel).performClick()
        
        val listName = "To Delete"
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.hint_listName)).performTextInput(listName)
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.create)).performClick()

        // Trigger delete via swipe
        // Note: SwipeToDeleteOrEdit uses DismissDirection.StartToEnd for delete (Red background)
        composeTestRule.onNodeWithText(listName).performTouchInput {
            swipeRight()
        }

        // Verify dialog appears
        val confirmationMessage = composeTestRule.activity.getString(R.string.homeScreen_deleteConfirmation)
        composeTestRule.onNodeWithText(confirmationMessage).assertIsDisplayed()
    }
}

    @Test
    fun addingListUpdatesUI() {
        val addUserListLabel = composeTestRule.activity.getString(R.string.contentDescription_addUserList)
        composeTestRule.onNodeWithContentDescription(addUserListLabel).performClick()
        
        val listName = "New UI List"
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.hint_listName)).performTextInput(listName)
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.create)).performClick()

        composeTestRule.onNodeWithText(listName).assertIsDisplayed()
        @Test
    fun deleteConfirmationDialogAppears() {
        val addUserListLabel = composeTestRule.activity.getString(R.string.contentDescription_addUserList)
        composeTestRule.onNodeWithContentDescription(addUserListLabel).performClick()
        
        val listName = "To Delete"
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.hint_listName)).performTextInput(listName)
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.create)).performClick()

        // Trigger delete via swipe
        // Note: SwipeToDeleteOrEdit uses DismissDirection.StartToEnd for delete (Red background)
        composeTestRule.onNodeWithText(listName).performTouchInput {
            swipeRight()
        }

        // Verify dialog appears
        val confirmationMessage = composeTestRule.activity.getString(R.string.homeScreen_deleteConfirmation)
        composeTestRule.onNodeWithText(confirmationMessage).assertIsDisplayed()
    }
}

    @Test
    fun newListDialog_errorStates() {
        val addUserListLabel = composeTestRule.activity.getString(R.string.contentDescription_addUserList)
        composeTestRule.onNodeWithContentDescription(addUserListLabel).performClick()

        // Initially empty, buttons should be disabled
        val createLabel = composeTestRule.activity.getString(R.string.create)
        val createAnotherLabel = composeTestRule.activity.getString(R.string.createAnother)
        
        composeTestRule.onNodeWithText(createLabel).assertIsNotEnabled()
        composeTestRule.onNodeWithText(createAnotherLabel).assertIsNotEnabled()

        // Type something, buttons should be enabled
        val hint = composeTestRule.activity.getString(R.string.hint_listName)
        composeTestRule.onNodeWithText(hint).performTextInput("Valid Name")
        composeTestRule.onNodeWithText(createLabel).assertIsEnabled()
        composeTestRule.onNodeWithText(createAnotherLabel).assertIsEnabled()

        // Clear text, buttons should be disabled again and error message shown
        composeTestRule.onNodeWithText("Valid Name").performTextClearance()
        composeTestRule.onNodeWithText(createLabel).assertIsNotEnabled()
        
        val errorText = composeTestRule.activity.getString(R.string.error_listNameMustNotBeEmpty)
        composeTestRule.onNodeWithText(errorText).assertIsDisplayed()
        @Test
    fun deleteConfirmationDialogAppears() {
        val addUserListLabel = composeTestRule.activity.getString(R.string.contentDescription_addUserList)
        composeTestRule.onNodeWithContentDescription(addUserListLabel).performClick()
        
        val listName = "To Delete"
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.hint_listName)).performTextInput(listName)
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.create)).performClick()

        // Trigger delete via swipe
        // Note: SwipeToDeleteOrEdit uses DismissDirection.StartToEnd for delete (Red background)
        composeTestRule.onNodeWithText(listName).performTouchInput {
            swipeRight()
        }

        // Verify dialog appears
        val confirmationMessage = composeTestRule.activity.getString(R.string.homeScreen_deleteConfirmation)
        composeTestRule.onNodeWithText(confirmationMessage).assertIsDisplayed()
    }
}

    @Test
    fun newListDialog_createAnotherKeepsDialogOpen() {
        val addUserListLabel = composeTestRule.activity.getString(R.string.contentDescription_addUserList)
        composeTestRule.onNodeWithContentDescription(addUserListLabel).performClick()

        val hint = composeTestRule.activity.getString(R.string.hint_listName)
        val createAnotherLabel = composeTestRule.activity.getString(R.string.createAnother)

        // Add first list
        composeTestRule.onNodeWithText(hint).performTextInput("List 1")
        composeTestRule.onNodeWithText(createAnotherLabel).performClick()

        // Dialog should still be open (hint visible)
        composeTestRule.onNodeWithText(hint).assertIsDisplayed()
        
        // Input should be cleared
        composeTestRule.onNodeWithText("List 1").assertDoesNotExist()

        // Dismiss dialog
        val cancelLabel = composeTestRule.activity.getString(R.string.cancel)
        composeTestRule.onNodeWithText(cancelLabel).performClick()

        // Verify "List 1" was actually added to the screen
        composeTestRule.onNodeWithText("List 1").assertIsDisplayed()
        @Test
    fun deleteConfirmationDialogAppears() {
        val addUserListLabel = composeTestRule.activity.getString(R.string.contentDescription_addUserList)
        composeTestRule.onNodeWithContentDescription(addUserListLabel).performClick()
        
        val listName = "To Delete"
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.hint_listName)).performTextInput(listName)
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.create)).performClick()

        // Trigger delete via swipe
        // Note: SwipeToDeleteOrEdit uses DismissDirection.StartToEnd for delete (Red background)
        composeTestRule.onNodeWithText(listName).performTouchInput {
            swipeRight()
        }

        // Verify dialog appears
        val confirmationMessage = composeTestRule.activity.getString(R.string.homeScreen_deleteConfirmation)
        composeTestRule.onNodeWithText(confirmationMessage).assertIsDisplayed()
    }
}
    @Test
    fun deleteConfirmationDialogAppears() {
        val addUserListLabel = composeTestRule.activity.getString(R.string.contentDescription_addUserList)
        composeTestRule.onNodeWithContentDescription(addUserListLabel).performClick()
        
        val listName = "To Delete"
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.hint_listName)).performTextInput(listName)
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.create)).performClick()

        // Trigger delete via swipe
        // Note: SwipeToDeleteOrEdit uses DismissDirection.StartToEnd for delete (Red background)
        composeTestRule.onNodeWithText(listName).performTouchInput {
            swipeRight()
        }

        // Verify dialog appears
        val confirmationMessage = composeTestRule.activity.getString(R.string.homeScreen_deleteConfirmation)
        composeTestRule.onNodeWithText(confirmationMessage).assertIsDisplayed()
    }
}
