package com.hotmail.or_dvir.sabinesList.database.repositories

import com.hotmail.or_dvir.sabinesList.models.ListItem
import kotlinx.coroutines.flow.Flow

interface ListItemsRepository {
    fun getAllByAlphabet(listId: Int): Flow<List<ListItem>>
    suspend fun insertOrReplace(listItem: ListItem): Long
    suspend fun rename(itemId: Int, newName: String): Int
    suspend fun changeCheckedState(itemId: Int, isChecked: Boolean): Int
    suspend fun markAllUnchecked(listId: Int): Int
    suspend fun delete(listItemId: Int)
    suspend fun search(query: String): List<ListItem>
}
