package com.hotmail.or_dvir.sabinesList.database.repositories

import com.hotmail.or_dvir.sabinesList.database.daos.ListItemDao
import com.hotmail.or_dvir.sabinesList.models.ListItem
import com.hotmail.or_dvir.sabinesList.toEntity
import com.hotmail.or_dvir.sabinesList.toListItems
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ListItemsRepositoryImpl @Inject constructor(
    private val dao: ListItemDao,
    private val scopeThatShouldNotBeCancelled: CoroutineScope,
    private val dispatcher: CoroutineDispatcher
) : ListItemsRepository {

    // todo for now assume all operations are successful

    override fun getAllByStartDateDesc(listId: Int): Flow<List<ListItem>> =
        dao.getAll(listId).map { entities ->
            entities.toListItems().sortedWith(
                compareByDescending<ListItem> { it.startDate }
                    .thenByDescending { it.startTime }
            )
        }

    override suspend fun insertOrReplace(listItem: ListItem): Long {
        return shouldNotBeCancelled(
            dispatcher = dispatcher,
            scopeThatShouldNotBeCancelled = scopeThatShouldNotBeCancelled
        ) {
            dao.insertOrReplace(listItem.toEntity())
        }
    }

    override suspend fun delete(listItemId: Int) {
        return shouldNotBeCancelled(
            dispatcher = dispatcher,
            scopeThatShouldNotBeCancelled = scopeThatShouldNotBeCancelled
        ) {
            dao.delete(listItemId)
        }
    }
}
