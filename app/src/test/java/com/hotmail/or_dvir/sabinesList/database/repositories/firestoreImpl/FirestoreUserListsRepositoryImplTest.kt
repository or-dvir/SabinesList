package com.hotmail.or_dvir.sabinesList.database.repositories.firestoreImpl

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.hotmail.or_dvir.sabinesList.database.firestoreDocuments.UserListDocument
import com.hotmail.or_dvir.sabinesList.models.UserList
import com.hotmail.or_dvir.sabinesList.userListsCollection
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FirestoreUserListsRepositoryImplTest {

    private val firestore: FirebaseFirestore = mockk()
    private val auth: FirebaseAuth = mockk()
    private val dispatcher: CoroutineDispatcher = UnconfinedTestDispatcher()
    private val scope: CoroutineScope = CoroutineScope(dispatcher)
    private val user: FirebaseUser = mockk()

    private lateinit var repository: FirestoreUserListsRepositoryImpl

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        mockkStatic("com.hotmail.or_dvir.sabinesList.FirebaseExtKt")
        mockkStatic("kotlinx.coroutines.tasks.TasksKt")
        every { auth.currentUser } returns user
        repository = FirestoreUserListsRepositoryImpl(firestore, auth, scope, dispatcher)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `insert correctly maps UserList to UserListDocument`() = runTest {
        val userList = UserList(name = "My List", id = 123)
        val expectedDocument = UserListDocument(id = "123", name = "My List")
        val mockCollection: CollectionReference = mockk()

        // Mock the extension function
        every { firestore.userListsCollection(auth) } returns mockCollection
        // Mock the add operation
        every { mockCollection.add(expectedDocument) } returns Tasks.forResult(mockk())

        val result = repository.insert(userList)

        assertTrue(result.isSuccess)
        verify(exactly = 1) { mockCollection.add(expectedDocument) }
    }
}
