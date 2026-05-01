package com.hotmail.or_dvir.sabinesList.ui.userListsScreen

import app.cash.turbine.test
import com.hotmail.or_dvir.sabinesList.MainDispatcherRule
import com.hotmail.or_dvir.sabinesList.database.repositories.UserListsRepository
import com.hotmail.or_dvir.sabinesList.models.UserList
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
class UserListsScreenModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repo = mockk<UserListsRepository>()
    private lateinit var screenModel: UserListsScreenModel

    private val userListsFlow = MutableStateFlow<List<UserList>>(emptyList())

    @Before
    fun setup() {
        every { repo.getAllSortedByAlphabet() } returns userListsFlow
        screenModel = UserListsScreenModel(repo)
    }

    @Test
    fun `usersListsFlow emits all lists when search is inactive`() = runTest {
        val lists = listOf(UserList("A"), UserList("B"))
        userListsFlow.value = lists

        screenModel.usersListsFlow.test {
            assertEquals(lists, awaitItem())
        }
    }

    @Test
    fun `usersListsFlow filters lists when search is active`() = runTest {
        val lists = listOf(UserList("Apple"), UserList("Banana"))
        userListsFlow.value = lists

        screenModel.onUserEvent(BaseScreenModel.UserEvent.SearchActiveStateChanged(true))
        screenModel.onUserEvent(BaseScreenModel.UserEvent.SearchQueryChanged("app"))

        screenModel.usersListsFlow.test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Apple", result[0].name)
        }
    }

    @Test
    fun `onCreateNewList calls repo and sends side effect`() = runTest {
        val name = "New List"
        coEvery { repo.insertOrReplace(any()) } returns 1L

        screenModel.onUserEvent(UserListsScreenModel.UserListsEvent.CreateNewList(name))

        coVerify { repo.insertOrReplace(match { it.name == name }) }
        screenModel.sideEffectsFlow.test {
            val effect = awaitItem()
            assertTrue(effect is BaseScreenModel.SideEffect.ShowMessage)
        }
    }

    @Test
    fun `onDeleteList calls repo`() = runTest {
        val id = 123
        coEvery { repo.delete(id) } returns Unit

        screenModel.onUserEvent(UserListsScreenModel.UserListsEvent.DeleteList(id))

        coVerify { repo.delete(id) }
    }
}
