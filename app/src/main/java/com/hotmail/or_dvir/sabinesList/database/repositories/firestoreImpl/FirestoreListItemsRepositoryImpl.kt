package com.hotmail.or_dvir.sabinesList.database.repositories.firestoreImpl

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.hotmail.or_dvir.sabinesList.database.firestoreDocuments.ListItemDocument
import com.hotmail.or_dvir.sabinesList.database.repositories.FirestoreListItemsRepository
import com.hotmail.or_dvir.sabinesList.database.repositories.ListItemsResult
import com.hotmail.or_dvir.sabinesList.database.repositories.shouldNotBeCancelled
import com.hotmail.or_dvir.sabinesList.listsItemsCollection
import com.hotmail.or_dvir.sabinesList.models.ListItem
import com.hotmail.or_dvir.sabinesList.toDocument
import com.hotmail.or_dvir.sabinesList.toListItems
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

internal class FirestoreListItemsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val scopeThatShouldNotBeCancelled: CoroutineScope,
    private val dispatcher: CoroutineDispatcher
) : FirestoreListItemsRepository {

    private companion object {
        private val LOG_TAG: String = FirestoreListItemsRepositoryImpl::class.java.simpleName
    }

    private fun Throwable.log() = Log.e(LOG_TAG, message, this)

    private suspend inline fun doOnItemsCollection(
        listId: String,
        crossinline action: suspend (CollectionReference) -> Task<*>
    ): Result<Unit> = shouldNotBeCancelled(dispatcher, scopeThatShouldNotBeCancelled) {
        firestore.listsItemsCollection(auth, listId)?.let {
            try {
                action(it).await()
                Result.success(Unit)
            } catch (e: Exception) {
                e.log()
                Result.failure(e)
            }
        } ?: Result.failure(
            FirebaseFirestoreException(
                "user not logged in",
                FirebaseFirestoreException.Code.UNAUTHENTICATED
            )
        )
    }

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

    // todo should all the UI filtering (checked/unchecekd) be done here or keep existing logic
    //  where everything happens on the original list on-device?
    //  note that this could affect search functionality! and counts towards read/write (should also just move to happen on firestore?)

    override suspend fun update(listItem: ListItem, listId: String): Result<Unit> =
        doOnItemsCollection(listId) {
            it.document(listItem.id).set(listItem.toDocument())
        }

    override suspend fun insert(listItem: ListItem, listId: String) =
        doOnItemsCollection(listId) {
            it.add(listItem.toDocument())
        }

    override suspend fun markAllUnchecked(listId: String): Result<Unit> =
        doOnItemsCollection(listId) { itemsCol ->
            val batch = firestore.batch()

            itemsCol.get().await().forEach { doc ->
                batch.update(doc.reference, ListItemDocument.PROPERTY_IS_CHECKED, false)
            }

            batch.commit()
        }

    override suspend fun delete(listItemId: String, listId: String): Result<Unit> =
        doOnItemsCollection(listId) {
            it.document(listItemId).delete()
        }
}
