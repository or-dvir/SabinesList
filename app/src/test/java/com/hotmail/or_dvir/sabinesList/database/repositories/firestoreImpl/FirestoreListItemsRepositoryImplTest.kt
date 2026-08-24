package com.hotmail.or_dvir.sabinesList.database.repositories.firestoreImpl

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.WriteBatch
import com.hotmail.or_dvir.sabinesList.database.firestoreDocuments.ListItemDocument
import com.hotmail.or_dvir.sabinesList.listsItemsCollection
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FirestoreListItemsRepositoryImplTest {

    private val firestore: FirebaseFirestore = mockk()
    private val auth: FirebaseAuth = mockk()
    private val dispatcher: CoroutineDispatcher = UnconfinedTestDispatcher()
    private val scope: CoroutineScope = CoroutineScope(dispatcher)
    private val user: FirebaseUser = mockk()

    private lateinit var repository: FirestoreListItemsRepositoryImpl

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        mockkStatic("com.hotmail.or_dvir.sabinesList.FirebaseExtKt")
        mockkStatic("kotlinx.coroutines.tasks.TasksKt")
        every { auth.currentUser } returns user
        repository = FirestoreListItemsRepositoryImpl(firestore, auth, scope, dispatcher)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `markAllUnchecked builds and commits a batch for all items`() = runTest {
        val listId = "list1"
        val mockCollection: CollectionReference = mockk()
        val mockQuerySnapshot: QuerySnapshot = mockk()
        val mockDoc1: QueryDocumentSnapshot = mockk()
        val mockDoc2: QueryDocumentSnapshot = mockk()
        val mockRef1: DocumentReference = mockk()
        val mockRef2: DocumentReference = mockk()
        val mockBatch: WriteBatch = mockk()

        // Mock getting the collection via extension
        every { firestore.listsItemsCollection(auth, listId) } returns mockCollection

        // Mock the fetch
        every { mockCollection.get() } returns Tasks.forResult(mockQuerySnapshot)
        every { mockQuerySnapshot.iterator() } returns mutableListOf(mockDoc1, mockDoc2).iterator()
        every { mockDoc1.reference } returns mockRef1
        every { mockDoc2.reference } returns mockRef2

        // Mock the batch
        every { firestore.batch() } returns mockBatch
        every {
            mockBatch.update(
                any<DocumentReference>(),
                any<String>(),
                any()
            )
        } returns mockBatch
        every { mockBatch.commit() } returns Tasks.forResult(null)

        val result = repository.markAllUnchecked(listId)

        assert(result.isSuccess)
        verify(exactly = 1) {
            mockBatch.update(
                mockRef1,
                ListItemDocument.PROPERTY_IS_CHECKED,
                false
            )
        }
        verify(exactly = 1) {
            mockBatch.update(
                mockRef2,
                ListItemDocument.PROPERTY_IS_CHECKED,
                false
            )
        }
        verify(exactly = 1) { mockBatch.commit() }
    }

    @Test
    fun `markAllUnchecked does not commit batch if fetch fails`() = runTest {
        val listId = "list1"
        val mockCollection: CollectionReference = mockk()
        val exception = Exception("fetch failed")

        // Mock getting the collection via extension
        every { firestore.listsItemsCollection(auth, listId) } returns mockCollection

        // Mock the fetch failure
        every { mockCollection.get() } returns Tasks.forException(exception)

        val result = repository.markAllUnchecked(listId)

        assert(result.isFailure)
        verify(exactly = 0) { firestore.batch() }
    }
}
