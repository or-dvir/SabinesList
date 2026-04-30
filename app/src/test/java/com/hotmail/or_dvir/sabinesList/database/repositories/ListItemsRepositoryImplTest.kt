package com.hotmail.or_dvir.sabinesList.database.repositories

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.hotmail.or_dvir.sabinesList.database.AppDatabase
import com.hotmail.or_dvir.sabinesList.database.daos.ListItemDao
import com.hotmail.or_dvir.sabinesList.database.daos.UserListDao
import com.hotmail.or_dvir.sabinesList.database.entities.UserListEntity
import com.hotmail.or_dvir.sabinesList.models.ListItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class ListItemsRepositoryImplTest {

    private lateinit var db: AppDatabase
    private lateinit var listItemDao: ListItemDao
    private lateinit var userListDao: UserListDao
    private lateinit var repository: ListItemsRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private var userListId: Int = 0

    // todo can this be extracted? seems to be duplicate code (almost)
    //  same for some of the above vals/vars
    //  honestly probably not worth it because i am going to replace it with firebase firestore
    @Before
    fun setup() = runTest(testDispatcher) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        listItemDao = db.listItemsDao()
        userListDao = db.userListsDao()
        repository = ListItemsRepositoryImpl(listItemDao, testScope, testDispatcher)

        // Create a parent list
        userListId = userListDao.insertOrReplace(UserListEntity(id = 0, name = "Parent")).toInt()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun `insertOrReplace inserts an item and getAllByAlphabet returns it`() = runTest(testDispatcher) {
        val itemName = "My Item"
        val item = ListItem(itemName, userListId, false)
        repository.insertOrReplace(item)

        repository.getAllByAlphabet(userListId).test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals(itemName, result[0].name)
        }
    }

    @Test
    fun `changeCheckedState updates the item`() = runTest(testDispatcher) {
        // todo worth extracting to a shared function?
        val item = ListItem("Item", userListId, false)
        val id = repository.insertOrReplace(item).toInt()

        repository.changeCheckedState(id, true)

        repository.getAllByAlphabet(userListId).test {
            val result = awaitItem()
            // todo replace with assertTrue (also in other places)
            assertEquals(true, result[0].isChecked)
        }
    }

    @Test
    fun `markAllUnchecked updates all items in that list`() = runTest(testDispatcher) {
        repository.insertOrReplace(ListItem("Item 1", userListId, true))
        repository.insertOrReplace(ListItem("Item 2", userListId, true))

        repository.markAllUnchecked(userListId)

        repository.getAllByAlphabet(userListId).test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals(false, result[0].isChecked)
            assertEquals(false, result[1].isChecked)
        }
    }
}
// todo what about deleting an item?