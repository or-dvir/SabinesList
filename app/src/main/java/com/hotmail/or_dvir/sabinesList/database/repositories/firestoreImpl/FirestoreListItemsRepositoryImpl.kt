package com.hotmail.or_dvir.sabinesList.database.repositories.firestoreImpl

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.hotmail.or_dvir.sabinesList.database.firestoreDocuments.ListItemDocument
import com.hotmail.or_dvir.sabinesList.database.repositories.ListItemsRepository
import com.hotmail.or_dvir.sabinesList.database.repositories.ListItemsResult
import com.hotmail.or_dvir.sabinesList.listsItemsCollection
import com.hotmail.or_dvir.sabinesList.models.ListItem
import com.hotmail.or_dvir.sabinesList.toDocument
import com.hotmail.or_dvir.sabinesList.toListItems
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

// todo add to hilt
internal class FirestoreListItemsRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ListItemsRepository {

    private companion object {
        private val LOG_TAG: String = FirestoreListItemsRepositoryImpl::class.java.simpleName
    }

    private fun Throwable.log() = Log.e(LOG_TAG, message, this)

    private inline fun doOnItemsCollection(
        listId: String,
        action: (CollectionReference) -> Result<Unit>
    ): Result<Unit> =
        firestore.listsItemsCollection(auth, listId)?.let { action(it) }
            ?: Result.failure(
                FirebaseFirestoreException(
                    "user not logged in",
                    FirebaseFirestoreException.Code.UNAUTHENTICATED
                )
            )

    override fun getAllByAlphabet(listId: String): Flow<ListItemsResult> = callbackFlow {

        val listener = firestore.listsItemsCollection(auth, listId)
            ?.addSnapshotListener { snapshot, exception ->
                val result = if (exception != null) {
                    exception.log()
                    ListItemsResult.Error
                } else {
                    ListItemsResult.Success(
                        snapshot
                            ?.toObjects(ListItemDocument::class.java)
                            ?.toListItems()
                            ?.sortedBy { it.name }
                            ?: emptyList()
                    )
                }

                trySend(result).onFailure { e -> e?.log() }
            }

        awaitClose { listener?.remove() }
    }

    // todo should all the filtering (checked/unchecekd) be done here or keep existing logic
    //  where everything happens on the original list on-device?
    //  note that this could affect search functionality! (should also just move to happen on firestore?)

    override suspend fun edit(listItem: ListItem, listId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(listItem: ListItem, listId: String): Result<Unit> {
        return doOnItemsCollection(listId) {
            try {
                it.add(listItem.toDocument()).await()
                Result.success(Unit)
            } catch (e: Exception) {
                e.log()
                Result.failure(e)
            }
        }
    }


    override suspend fun rename(itemId: String, newName: String, listId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun changeCheckedState(
        itemId: String,
        isChecked: Boolean,
        listId: String
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun markAllUnchecked(listId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(listItemId: String, listId: String): Result<Unit> {
        TODO("Not yet implemented")
    }
}
