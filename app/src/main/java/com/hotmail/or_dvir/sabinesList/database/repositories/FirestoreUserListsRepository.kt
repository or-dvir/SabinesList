package com.hotmail.or_dvir.sabinesList.database.repositories

import com.hotmail.or_dvir.sabinesList.models.ListItem
import com.hotmail.or_dvir.sabinesList.models.UserList
import kotlinx.coroutines.flow.Flow

internal interface FirestoreUserListsRepository {
    fun getAllSortedByAlphabet(): Flow<List<UserList>>
    suspend fun insert(list: UserList): Result<Unit>
    suspend fun update(list: UserList): Result<Unit>
    suspend fun delete(listId: String): Result<Unit>
}

internal sealed class UserListsResult {
    data class Success(val data: List<UserList>) : ListItemsResult()
    object Error : ListItemsResult()
}
