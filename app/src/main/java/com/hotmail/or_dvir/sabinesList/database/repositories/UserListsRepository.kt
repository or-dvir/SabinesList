package com.hotmail.or_dvir.sabinesList.database.repositories

import com.hotmail.or_dvir.sabinesList.models.UserList
import kotlinx.coroutines.flow.Flow

interface UserListsRepository {
    fun getAllSortedByAlphabet(): Flow<List<UserList>>
    suspend fun insertOrReplace(userList: UserList): Long
    suspend fun delete(listId: Int)
    suspend fun search(query: String): List<UserList>
}
