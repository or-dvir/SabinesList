package com.hotmail.or_dvir.sabinesList.database.repositories

import com.hotmail.or_dvir.sabinesList.database.daos.UserListDao
import com.hotmail.or_dvir.sabinesList.models.UserList
import com.hotmail.or_dvir.sabinesList.toEntity
import com.hotmail.or_dvir.sabinesList.toUserLists
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class UserListsRepositoryImpl @Inject constructor(
    private val dao: UserListDao,
    private val scopeThatShouldNotBeCancelled: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
) : UserListsRepository {

    // todo for now assume all operations are successful

    override fun getAllSortedByAlphabet(): Flow<List<UserList>> =
        dao.getAllSortedByAlphabet().map { it.toUserLists() }

    override suspend fun insertOrReplace(userList: UserList): Long {
        return shouldNotBeCancelled(
            dispatcher = dispatcher,
            scopeThatShouldNotBeCancelled = scopeThatShouldNotBeCancelled
        ) {
            dao.insertOrReplace(userList.toEntity())
        }
    }

    override suspend fun delete(listId: Int) {
        shouldNotBeCancelled(
            dispatcher = dispatcher,
            scopeThatShouldNotBeCancelled = scopeThatShouldNotBeCancelled
        ) {
            dao.delete(listId)
        }
    }

    override suspend fun search(query: String): List<String> {
        return withContext(dispatcher) {
            dao.search(query)
        }
    }
}
