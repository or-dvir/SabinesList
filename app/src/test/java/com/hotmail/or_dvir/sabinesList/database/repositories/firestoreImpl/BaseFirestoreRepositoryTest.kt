package com.hotmail.or_dvir.sabinesList.database.repositories.firestoreImpl

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BaseFirestoreRepositoryTest {

    private val firestore: FirebaseFirestore = mockk()
    private val auth: FirebaseAuth = mockk()
    private val dispatcher: CoroutineDispatcher = UnconfinedTestDispatcher()
    private val scope: CoroutineScope = CoroutineScope(dispatcher)

    private lateinit var repository: TestFirestoreRepository

    internal class TestFirestoreRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher
    ) : BaseFirestoreRepository(firestore, auth, scope, dispatcher) {
        override val logTag: String = "TestRepo"

        suspend fun testDoOnCollection(
            getCollection: () -> CollectionReference?,
            action: suspend (CollectionReference) -> Task<*>
        ) = doOnCollection(getCollection, action)
    }

    @Before
    fun setup() {
        repository = TestFirestoreRepository(firestore, auth, scope, dispatcher)
        mockkStatic("kotlinx.coroutines.tasks.TasksKt")
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `doOnCollection returns failure with UnauthenticatedException when user is null`() = runTest {
        every { auth.currentUser } returns null

        val result = repository.testDoOnCollection({ mockk() }, { Tasks.forResult(null) })

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is UnauthenticatedException)
    }

    @Test
    fun `doOnCollection returns failure with CollectionUnavailableException when collection is null`() = runTest {
        val mockUser: FirebaseUser = mockk()
        every { auth.currentUser } returns mockUser

        val result = repository.testDoOnCollection({ null }, { Tasks.forResult(null) })

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CollectionUnavailableException)
    }

    @Test
    fun `doOnCollection returns failure when action throws FirebaseFirestoreException`() = runTest {
        val mockUser: FirebaseUser = mockk()
        val mockCollection: CollectionReference = mockk()
        val mockException = FirebaseFirestoreException("Server Error", FirebaseFirestoreException.Code.PERMISSION_DENIED)

        every { auth.currentUser } returns mockUser
        
        val result = repository.testDoOnCollection({ mockCollection }) {
            throw mockException
        }

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is FirebaseFirestoreException)
    }

    @Test(expected = CancellationException::class)
    fun `doOnCollection rethrows CancellationException`() = runTest {
        val mockUser: FirebaseUser = mockk()
        val mockCollection: CollectionReference = mockk()

        every { auth.currentUser } returns mockUser
        
        repository.testDoOnCollection({ mockCollection }) {
            throw CancellationException("Cancelled")
        }
    }
}
