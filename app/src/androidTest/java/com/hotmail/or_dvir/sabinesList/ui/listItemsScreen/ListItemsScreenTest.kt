package com.hotmail.or_dvir.sabinesList.ui.listItemsScreen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.hotmail.or_dvir.sabinesList.R
import com.hotmail.or_dvir.sabinesList.database.repositories.ListItemsRepository
import com.hotmail.or_dvir.sabinesList.database.repositories.UserListsRepository
import com.hotmail.or_dvir.sabinesList.models.ListItem
import com.hotmail.or_dvir.sabinesList.models.UserList
import com.hotmail.or_dvir.sabinesList.ui.mainActivity.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

private const val TEST_LIST_NAME = "Test List"
private const val TEST_ITEM_NAME = "Test Item"

@HiltAndroidTest
class ListItemsScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var userListsRepo: UserListsRepository
    @Inject
    lateinit var listItemsRepo: ListItemsRepository
    @Inject
    lateinit var db: com.hotmail.or_dvir.sabinesList.database.AppDatabase

    private var userListId: Int = 0

    @Before
    fun setup() {
        hiltRule.inject()
        db.clearAllTables()
        
        runBlocking {
            // Pre-populate a list and navigate to it
            userListId = userListsRepo.insertOrReplace(UserList(name = TEST_LIST_NAME)).toInt()
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(TEST_LIST_NAME).performClick()
    }

    @Test
    fun uncheckAllConfirmationDialogAppears() {
        // Add an item first
        runBlocking {
            listItemsRepo.insertOrReplace(ListItem(name = TEST_ITEM_NAME, listId = userListId, isChecked = false))
        }
        composeTestRule.waitForIdle()

        val uncheckAllLabel = composeTestRule.activity.getString(R.string.menuItem_uncheckAll)
        composeTestRule.onNodeWithContentDescription(uncheckAllLabel).performClick()

        // Verify confirmation dialog
        val confirmationMessage = composeTestRule.activity.getString(R.string.listItemsScreen_uncheckAllConfirmation).trim()
        composeTestRule.onNodeWithText(confirmationMessage).assertIsDisplayed()
    }

    @Test
    fun navigatesToCorrectList() {
        // Verify title matches list name
        composeTestRule.onNodeWithText(TEST_LIST_NAME).assertIsDisplayed()
    }

    @Test
    fun dynamicFiltering_uncheckedItemDisappearsFromCheckedFilter() {
        // Add a checked item
        val itemName = "To Be Unchecked"
        runBlocking {
            listItemsRepo.insertOrReplace(ListItem(name = itemName, listId = userListId, isChecked = true))
        }
        composeTestRule.waitForIdle()
        
        // Go to "Checked" filter
        val checkedFilterLabel = composeTestRule.activity.getString(R.string.bottomNavigation_checked)
        composeTestRule.onNodeWithText(checkedFilterLabel).performClick()
        
        // Item SHOULD be there (now checked)
        val checkedDescription = composeTestRule.activity.getString(R.string.contentDescription_checkbox_checked, itemName)
        composeTestRule.onNodeWithText(itemName).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(checkedDescription).assertIsDisplayed()
        
        // Uncheck it
        composeTestRule.onNodeWithContentDescription(checkedDescription).performClick()

        composeTestRule.waitForIdle()

        // Item should DISAPPEAR immediately from this view
        composeTestRule.onNodeWithText(itemName).assertDoesNotExist()
    }

    @Test
    fun searchHint_isCorrect() {
        // Pre-populate an item so the search icon appears
        runBlocking {
            listItemsRepo.insertOrReplace(ListItem(name = TEST_ITEM_NAME, listId = userListId, isChecked = false))
        }
        composeTestRule.waitForIdle()

        val searchLabel = composeTestRule.activity.getString(R.string.menuItem_search)
        composeTestRule.onNodeWithContentDescription(searchLabel).performClick()
        
        val searchHint = composeTestRule.activity.getString(R.string.searchHint_items)
        composeTestRule.onNodeWithText(searchHint).assertIsDisplayed()
    }
}
