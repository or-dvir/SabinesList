//package com.hotmail.or_dvir.sabinesList.database.repositories
//
//import android.content.Context
//import androidx.room.Room
//import androidx.test.core.app.ApplicationProvider
//import app.cash.turbine.test
//import com.hotmail.or_dvir.sabinesList.database.AppDatabase
//import com.hotmail.or_dvir.sabinesList.database.daos.UserListDao
//import com.hotmail.or_dvir.sabinesList.models.UserList
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.StandardTestDispatcher
//import kotlinx.coroutines.test.TestScope
//import kotlinx.coroutines.test.runTest
//import org.junit.After
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.robolectric.RobolectricTestRunner
//
//@ExperimentalCoroutinesApi
//@RunWith(RobolectricTestRunner::class)
//class UserListsRepositoryImplTest {
//
//    private lateinit var db: AppDatabase
//    private lateinit var dao: UserListDao
//    private lateinit var repository: UserListsRepositoryImpl
//    private val testDispatcher = StandardTestDispatcher()
//    private val testScope = TestScope(testDispatcher)
//
//    @Before
//    fun setup() {
//        val context = ApplicationProvider.getApplicationContext<Context>()
//        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
//            .allowMainThreadQueries()
//            .build()
//        dao = db.userListsDao()
//        repository = UserListsRepositoryImpl(dao, testScope, testDispatcher)
//    }
//
//    @After
//    fun teardown() {
//        db.close()
//    }
//
//    @Test
//    fun `insertOrReplace inserts a list and getAllSortedByAlphabet returns it`() = runTest(testDispatcher) {
//        val listName = "My List"
//        val list = UserList(listName)
//        repository.insertOrReplace(list)
//
//        repository.getAllSortedByAlphabet().test {
//            val result = awaitItem()
//            assertEquals(1, result.size)
//            assertEquals(listName, result[0].name)
//        }
//    }
//
//    @Test
//    fun `delete removes the list`() = runTest(testDispatcher) {
//        val list = UserList("To Delete")
//        // id 0 for auto-generation is handled by the model->entity conversion in the repository
//        val id = repository.insertOrReplace(list).toInt()
//
//        repository.delete(id)
//
//        repository.getAllSortedByAlphabet().test {
//            assertEquals(emptyList<UserList>(), awaitItem())
//        }
//    }
//}
