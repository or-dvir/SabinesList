package com.hotmail.or_dvir.sabinesList.database.repositories.firestoreImpl

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hotmail.or_dvir.sabinesList.database.firestoreDocuments.ListItemDocument
import com.hotmail.or_dvir.sabinesList.database.repositories.ListItemsRepository
import com.hotmail.or_dvir.sabinesList.database.repositories.ListItemsResult
import com.hotmail.or_dvir.sabinesList.listsItemsCollection
import com.hotmail.or_dvir.sabinesList.models.ListItem
import com.hotmail.or_dvir.sabinesList.toListItems
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

// todo add to hilt
internal class FirestoreListItemsRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ListItemsRepository {

    private companion object {
        val LOG_TAG: String = FirestoreListItemsRepositoryImpl::class.java.simpleName
    }

    override fun getAllByAlphabet(listId: String): Flow<ListItemsResult> = callbackFlow {
        val listener =
            firestore.listsItemsCollection(auth, listId)
                ?.addSnapshotListener { snapshot, exception ->
                    val result = if (exception != null) {
                        Log.e(LOG_TAG, exception.message, exception)
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

                    trySend(result).onFailure { e ->
                        Log.e(LOG_TAG, "trySend failed", e)
                    }
                }

        awaitClose { listener?.remove() }
    }

    override suspend fun insertOrReplace(listItem: ListItem): Long {
        TODO("Not yet implemented")
    }

    override suspend fun rename(itemId: String, newName: String): Int {
        TODO("Not yet implemented")
    }

    override suspend fun changeCheckedState(
        itemId: String,
        isChecked: Boolean
    ): Int {
        TODO("Not yet implemented")
    }

    override suspend fun markAllUnchecked(listId: String): Int {
        TODO("Not yet implemented")
    }

    override suspend fun delete(listItemId: String) {
        TODO("Not yet implemented")
    }
}
