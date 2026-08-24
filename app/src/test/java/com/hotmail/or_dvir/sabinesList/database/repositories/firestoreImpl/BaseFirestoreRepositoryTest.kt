package com.hotmail.or_dvir.sabinesList.database.repositories.firestoreImpl

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.CancellationException
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
class BaseFirestoreRepositoryTest {

    private val firestore: FirebaseFirestore = mockk()
    private val auth: FirebaseAuth = mockk()
    private val dispatcher: CoroutineDispatcher = UnconfinedTestDispatcher()
    private val scope: CoroutineScope = CoroutineScope(dispatcher)

    private lateinit var repository: TestFirestoreRepository

    // Simple subclass to test the abstract BaseFirestoreRepository
    internal class TestFirestoreRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher
    ) : BaseFirestoreRepository(firestore, auth, scope, dispatcher) {
        override val logTag: String = "TestRepo"
    }

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

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

        // Using throwaway mock for collection since logic fails before accessing it
        val result = repository.doOnCollection({ mockk<CollectionReference>() }, { Tasks.forResult(null) })

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is UnauthenticatedException)
    }

    @Test
    fun `doOnCollection returns failure with CollectionUnavailableException when collection is null`() = runTest {
        val mockUser: FirebaseUser = mockk()
        every { auth.currentUser } returns mockUser

        val result = repository.doOnCollection({ null }, { Tasks.forResult(null) })

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CollectionUnavailableException)
    }

    @Test
    fun `doOnCollection returns failure when action throws FirebaseFirestoreException`() = runTest {
        val mockUser: FirebaseUser = mockk()
        val mockCollection: CollectionReference = mockk()

        // We CANNOT mock FirebaseFirestoreException or its Code enum because the static
        // initializer of the Code enum uses android.util.SparseArray, which crashes the JVM test.
        // Instead, we throw a generic exception and ensure the repository returns it as a failure.
        // In production, BaseFirestoreRepository just catches 'Exception' and logs it.
        val testException = Exception("Firestore Error")

        every { auth.currentUser } returns mockUser

        val result = repository.doOnCollection({ mockCollection }) {
            throw testException
        }

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() === testException)
    }

    @Test(expected = CancellationException::class)
    fun `doOnCollection rethrows CancellationException`() = runTest {
        val mockUser: FirebaseUser = mockk()
        val mockCollection: CollectionReference = mockk()

        every { auth.currentUser } returns mockUser
        
        repository.doOnCollection({ mockCollection }) {
            throw CancellationException("Cancelled")
        }
    }
}
