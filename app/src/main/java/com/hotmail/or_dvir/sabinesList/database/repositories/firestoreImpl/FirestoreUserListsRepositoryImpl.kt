package com.hotmail.or_dvir.sabinesList.database.repositories.firestoreImpl

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.hotmail.or_dvir.sabinesList.database.repositories.FirestoreUserListsRepository
import com.hotmail.or_dvir.sabinesList.models.UserList
import com.hotmail.or_dvir.sabinesList.userListsCollection
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class FirestoreUserListsRepositoryImpl @Inject constructor(
    firestore: FirebaseFirestore,
    auth: FirebaseAuth,
    scopeThatShouldNotBeCancelled: CoroutineScope,
    dispatcher: CoroutineDispatcher
): BaseFirestoreRepository(firestore, auth, scopeThatShouldNotBeCancelled, dispatcher),
    FirestoreUserListsRepository {

    override val logTag: String = FirestoreUserListsRepositoryImpl::class.java.simpleName

    private suspend inline fun doOnListsCollection(
        crossinline action: suspend (CollectionReference) -> Task<*>
    ): Result<Unit> = doOnCollection(
        getCollection = { firestore.userListsCollection(auth) },
        action = action
    )

    override fun getAllSortedByAlphabet(): Flow<List<UserList>> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(list: UserList): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun update(list: UserList): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(listId: String): Result<Unit> {
        TODO("Not yet implemented")
    }
}
