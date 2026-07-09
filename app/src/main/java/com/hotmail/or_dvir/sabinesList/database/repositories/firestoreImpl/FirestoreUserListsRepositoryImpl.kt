package com.hotmail.or_dvir.sabinesList.database.repositories.firestoreImpl

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.hotmail.or_dvir.sabinesList.database.firestoreDocuments.UserListDocument
import com.hotmail.or_dvir.sabinesList.database.repositories.FirestoreUserListsRepository
import com.hotmail.or_dvir.sabinesList.database.repositories.UserListsResult
import com.hotmail.or_dvir.sabinesList.models.UserList
import com.hotmail.or_dvir.sabinesList.toDocument
import com.hotmail.or_dvir.sabinesList.toUserLists
import com.hotmail.or_dvir.sabinesList.userListsCollection
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

internal class FirestoreUserListsRepositoryImpl @Inject constructor(
    firestore: FirebaseFirestore,
    auth: FirebaseAuth,
    scopeThatShouldNotBeCancelled: CoroutineScope,
    dispatcher: CoroutineDispatcher
) : BaseFirestoreRepository(firestore, auth, scopeThatShouldNotBeCancelled, dispatcher),
    FirestoreUserListsRepository {

    override val logTag: String = FirestoreUserListsRepositoryImpl::class.java.simpleName

    private suspend inline fun doOnListsCollection(
        crossinline action: suspend (CollectionReference) -> Task<*>
    ): Result<Unit> = doOnCollection(
        getCollection = { firestore.userListsCollection(auth) },
        action = action
    )

    override fun getAllSortedByAlphabet(): Flow<UserListsResult> = callbackFlow {
        val listener = firestore.userListsCollection(auth)
            ?.addSnapshotListener { snapshot, exception ->
                val result = if (exception != null) {
                    exception.log()
                    UserListsResult.Error
                } else {
                    UserListsResult.Success(
                        snapshot
                            ?.toObjects(UserListDocument::class.java)
                            ?.toUserLists()
                            ?.sortedBy { it.name }
                            ?: emptyList()
                    )
                }

                trySend(result).onFailure { e -> e?.log() }
            }

        awaitClose { listener?.remove() }
    }

    override suspend fun insert(list: UserList): Result<Unit> =
        doOnListsCollection {
            it.add(list.toDocument())
        }

    override suspend fun update(list: UserList): Result<Unit> =
        doOnListsCollection {
            // todo toString() here is just temporary for compilation. the object itself needs
            //  to be changed to `val id: String`, then `toString()` should be removed here
            it.document(list.id.toString()).set(list.toDocument())
        }

    override suspend fun delete(listId: String): Result<Unit> =
        doOnListsCollection {
            it.document(listId).delete()
        }
}
