package com.hotmail.or_dvir.sabinesList.database.repositories

import com.hotmail.or_dvir.sabinesList.models.ListItem
import kotlinx.coroutines.flow.Flow

internal interface ListItemsRepository {
    fun getAllByAlphabet(listId: String): Flow<List<ListItem>>
    suspend fun insertOrReplace(listItem: ListItem): Long
    suspend fun rename(itemId: String, newName: String): Int
    suspend fun changeCheckedState(itemId: String, isChecked: Boolean): Int
    suspend fun markAllUnchecked(listId: String): Int
    suspend fun delete(listItemId: String)
}
