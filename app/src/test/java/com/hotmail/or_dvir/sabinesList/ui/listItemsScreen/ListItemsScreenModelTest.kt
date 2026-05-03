package com.hotmail.or_dvir.sabinesList.ui.listItemsScreen

import app.cash.turbine.test
import com.hotmail.or_dvir.sabinesList.MainDispatcherRule
import com.hotmail.or_dvir.sabinesList.database.repositories.ListItemsRepository
import com.hotmail.or_dvir.sabinesList.models.ListItem
import com.hotmail.or_dvir.sabinesList.ui.BaseScreenModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ListItemsScreenModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repo = mockk<ListItemsRepository>()
    private val userListId = 1
    private lateinit var screenModel: ListItemsScreenModel

    private val listItems = MutableStateFlow<List<ListItem>>(emptyList())

    @Before
    fun setup() {
        every { repo.getAllByAlphabet(userListId) } returns listItems
        screenModel = ListItemsScreenModel(userListId, repo)
    }

    @Test
    fun `listItems emits all items when search is inactive and filter is AllItems`() = runTest {
        val items = listOf(
            ListItem("A", userListId, false),
            ListItem("B", userListId, true)
        )
        listItems.value = items

        screenModel.listItems.test {
            assertEquals(items, awaitItem())
        }
    }

    @Test
    fun `listItems filters by CheckedItems`() = runTest {
        val checkedItem = ListItem("Checked", userListId, true)
        val items = listOf(
            ListItem("Unchecked", userListId, false),
            checkedItem
        )
        listItems.value = items

        screenModel.onUserEvent(ListItemsScreenModel.ListItemsEvent.BottomNavigationItemClicked(BottomNavigationListItem.CheckedItems))

        screenModel.listItems.test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Checked", result[0].name)
        }
    }

    @Test
    fun `listItems removes item when it becomes unchecked while CheckedItems filter is active`() = runTest {
        val item = ListItem("Item", userListId, true)
        listItems.value = listOf(item)

        screenModel.onUserEvent(ListItemsScreenModel.ListItemsEvent.BottomNavigationItemClicked(BottomNavigationListItem.CheckedItems))

        screenModel.listItems.test {
            // Initial emission with checked item
            assertEquals(listOf(item), awaitItem())

            // Simulate unchecking in the repository
            listItems.value = listOf(item.copy(isChecked = false))

            // Should emit empty list
            assertEquals(emptyList<ListItem>(), awaitItem())
        }
    }

    @Test
    fun `listItems removes item when it becomes checked while UncheckedItems filter is active`() = runTest {
        val item = ListItem("Item", userListId, false)
        listItems.value = listOf(item)

        screenModel.onUserEvent(ListItemsScreenModel.ListItemsEvent.BottomNavigationItemClicked(BottomNavigationListItem.UncheckedItems))

        screenModel.listItems.test {
            // Initial emission with unchecked item
            assertEquals(listOf(item), awaitItem())

            // Simulate checking in the repository
            listItems.value = listOf(item.copy(isChecked = true))

            // Should emit empty list
            assertEquals(emptyList<ListItem>(), awaitItem())
        }
    }

    @Test
    fun `listItems updates item but keeps it in list when checked while AllItems filter is active`() = runTest {
        val item = ListItem("Item", userListId, false)
        listItems.value = listOf(item)

        // AllItems is default, but let's be explicit
        screenModel.onUserEvent(ListItemsScreenModel.ListItemsEvent.BottomNavigationItemClicked(BottomNavigationListItem.AllItems))

        screenModel.listItems.test {
            assertEquals(listOf(item), awaitItem())

            val updatedItem = item.copy(isChecked = true)
            listItems.value = listOf(updatedItem)

            assertEquals(listOf(updatedItem), awaitItem())
        }
    }

    @Test
    fun `onCreateNewItem calls repo and sends side effect`() = runTest {
        val name = "New Item"
        coEvery { repo.insertOrReplace(any()) } returns 1L

        screenModel.onUserEvent(ListItemsScreenModel.ListItemsEvent.CreateNewItem(name))

        coVerify { repo.insertOrReplace(match { it.name == name && it.listId == userListId }) }
        screenModel.sideEffects.test {
            val effect = awaitItem()
            assertTrue(effect is BaseScreenModel.SideEffect.ShowMessage)
        }
    }

    @Test
    fun `onMarkAllUnchecked calls repo`() = runTest {
        coEvery { repo.markAllUnchecked(userListId) } returns 1

        screenModel.onUserEvent(ListItemsScreenModel.ListItemsEvent.MarkAllItemsUnchecked)

        coVerify { repo.markAllUnchecked(userListId) }
    }

    @Test
    fun `onChangeItemCheckedState calls repo`() = runTest {
        val itemId = 123
        val isChecked = true
        coEvery { repo.changeCheckedState(itemId, isChecked) } returns 1

        screenModel.onUserEvent(ListItemsScreenModel.ListItemsEvent.ChangeItemCheckedState(itemId, isChecked))

        coVerify { repo.changeCheckedState(itemId, isChecked) }
    }
}
