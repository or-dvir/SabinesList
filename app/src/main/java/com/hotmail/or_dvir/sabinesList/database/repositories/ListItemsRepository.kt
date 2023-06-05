package com.hotmail.or_dvir.sabinesList.database.repositories

import com.hotmail.or_dvir.sabinesList.models.ListItem
import kotlinx.coroutines.flow.Flow

interface ListItemsRepository {
    fun getAllByAlphabet(listId: Int): Flow<List<ListItem>>
    suspend fun insertOrReplace(listItem: ListItem): Long
    suspend fun delete(listItemId: Int)
}
