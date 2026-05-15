package com.hotmail.or_dvir.sabinesList.database.repositories

import com.hotmail.or_dvir.sabinesList.models.ListItem
import kotlinx.coroutines.flow.Flow

internal interface ListItemsRepository {
    fun getAllByAlphabet(listId: String): Flow<ListItemsResult>
    suspend fun insertOrReplace(listItem: ListItem): Result<Unit>
    suspend fun rename(itemId: String, newName: String): Result<Unit>
    suspend fun changeCheckedState(itemId: String, isChecked: Boolean): Result<Unit>
    suspend fun markAllUnchecked(listId: String): Result<Unit>
    suspend fun delete(listItemId: String): Result<Unit>
}

internal sealed class ListItemsResult {
    data class Success(val data: List<ListItem>) : ListItemsResult()
    object Error : ListItemsResult()
}
