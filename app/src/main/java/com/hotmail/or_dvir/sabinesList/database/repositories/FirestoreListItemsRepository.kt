package com.hotmail.or_dvir.sabinesList.database.repositories

import com.hotmail.or_dvir.sabinesList.models.ListItem
import kotlinx.coroutines.flow.Flow

internal interface FirestoreListItemsRepository {
    fun getAllByAlphabet(listId: String): Flow<ListItemsResult>
    suspend fun insert(listItem: ListItem, listId: String): Result<Unit>
    suspend fun update(listItem: ListItem, listId: String): Result<Unit>
    suspend fun markAllUnchecked(listId: String): Result<Unit>
    suspend fun delete(listItemId: String, listId: String): Result<Unit>
}

internal sealed class ListItemsResult {
    data class Success(val data: List<ListItem>) : ListItemsResult()
    object Error : ListItemsResult()
}
