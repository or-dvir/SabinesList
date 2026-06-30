package com.hotmail.or_dvir.sabinesList.database.repositories.firestoreImpl

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.hotmail.or_dvir.sabinesList.database.firestoreDocuments.ListItemDocument
import com.hotmail.or_dvir.sabinesList.database.repositories.FirestoreListItemsRepository
import com.hotmail.or_dvir.sabinesList.database.repositories.ListItemsResult
import com.hotmail.or_dvir.sabinesList.listsItemsCollection
import com.hotmail.or_dvir.sabinesList.models.ListItem
import com.hotmail.or_dvir.sabinesList.toDocument
import com.hotmail.or_dvir.sabinesList.toListItems
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

internal class FirestoreListItemsRepositoryImpl @Inject constructor(
    firestore: FirebaseFirestore,
    auth: FirebaseAuth,
    scopeThatShouldNotBeCancelled: CoroutineScope,
    dispatcher: CoroutineDispatcher
) : BaseFirestoreRepository(firestore, auth, scopeThatShouldNotBeCancelled, dispatcher),
    FirestoreListItemsRepository {

    override val logTag: String = FirestoreListItemsRepositoryImpl::class.java.simpleName

    private suspend inline fun doOnItemsCollection(
        listId: String,
        crossinline action: suspend (CollectionReference) -> Task<*>
    ): Result<Unit> = doOnCollection(
        getCollection = { firestore.listsItemsCollection(auth, listId) },
        action = action
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

    // todo should all the UI filtering (checked/unchecekd) be done here or keep existing logic
    //  where everything happens on the original list on-device?
    //  note that this could affect search functionality! and counts towards read/write (should also just move to happen on firestore?)

    override suspend fun update(listItem: ListItem, listId: String): Result<Unit> =
        doOnItemsCollection(listId) {
            // todo toString() here is just temporary for compilation. the object itself needs
            //  to be changed to `val id: String`, then `toString()` should be removed here
            it.document(listItem.id.toString()).set(listItem.toDocument())
        }

    override suspend fun insert(listItem: ListItem, listId: String) =
        doOnItemsCollection(listId) {
            it.add(listItem.toDocument())
        }

    override suspend fun markAllUnchecked(listId: String): Result<Unit> =
        doOnItemsCollection(listId) { itemsCol ->
            val batch = firestore.batch()

            itemsCol.get().await().forEach { doc ->
                currentCoroutineContext().ensureActive()
                batch.update(doc.reference, ListItemDocument.PROPERTY_IS_CHECKED, false)
            }
            currentCoroutineContext().ensureActive()
            batch.commit()
        }

    override suspend fun delete(listItemId: String, listId: String): Result<Unit> =
        doOnItemsCollection(listId) {
            it.document(listItemId).delete()
        }
}
