package com.hotmail.or_dvir.sabinesList.database.repositories.roomImpl

import com.hotmail.or_dvir.sabinesList.database.daos.ListItemDao
import com.hotmail.or_dvir.sabinesList.database.repositories.ListItemsRepository
import com.hotmail.or_dvir.sabinesList.database.repositories.shouldNotBeCancelled
import com.hotmail.or_dvir.sabinesList.models.ListItem
import com.hotmail.or_dvir.sabinesList.toEntity
import com.hotmail.or_dvir.sabinesList.toListItems
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RoomListItemsRepositoryImpl @Inject constructor(
    private val dao: ListItemDao,
    private val scopeThatShouldNotBeCancelled: CoroutineScope,
    private val dispatcher: CoroutineDispatcher
) : ListItemsRepository {

    // todo for now assume all operations are successful

    override fun getAllByAlphabet(listId: String): Flow<List<ListItem>> =
        dao.getAll(listId.toInt()).map { entities ->
            entities.toListItems().sortedBy { it.name }
        }

    override suspend fun rename(itemId: String, newName: String): Int {
        return shouldNotBeCancelled(
            dispatcher = dispatcher,
            scopeThatShouldNotBeCancelled = scopeThatShouldNotBeCancelled
        ) {
            dao.rename(itemId.toInt(), newName.trim())
        }
    }

    override suspend fun changeCheckedState(itemId: String, isChecked: Boolean): Int {
        return shouldNotBeCancelled(
            dispatcher = dispatcher,
            scopeThatShouldNotBeCancelled = scopeThatShouldNotBeCancelled
        ) {
            dao.changeCheckedState(itemId.toInt(), isChecked)
        }
    }

    override suspend fun insertOrReplace(listItem: ListItem): Long {
        return shouldNotBeCancelled(
            dispatcher = dispatcher,
            scopeThatShouldNotBeCancelled = scopeThatShouldNotBeCancelled
        ) {
            dao.insertOrReplace(listItem.toEntity())
        }
    }

    override suspend fun markAllUnchecked(listId: String): Int {
        return shouldNotBeCancelled(
            dispatcher = dispatcher,
            scopeThatShouldNotBeCancelled = scopeThatShouldNotBeCancelled
        ) {
            dao.markAllUnchecked(listId.toInt())
        }
    }

    override suspend fun delete(listItemId: String) {
        return shouldNotBeCancelled(
            dispatcher = dispatcher,
            scopeThatShouldNotBeCancelled = scopeThatShouldNotBeCancelled
        ) {
            dao.delete(listItemId.toInt())
        }
    }
}
