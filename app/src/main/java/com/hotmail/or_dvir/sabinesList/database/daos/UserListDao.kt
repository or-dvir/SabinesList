package com.hotmail.or_dvir.sabinesList.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hotmail.or_dvir.sabinesList.database.entities.UserListEntity
import com.hotmail.or_dvir.sabinesList.database.entities.UserListEntity.Companion.COLUMN_ID
import com.hotmail.or_dvir.sabinesList.database.entities.UserListEntity.Companion.COLUMN_NAME
import com.hotmail.or_dvir.sabinesList.database.entities.UserListEntity.Companion.TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface UserListDao {
    @Query("SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_NAME")
    fun getAllSortedByAlphabet(): Flow<List<UserListEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(userList: UserListEntity): Long

    @Query("DELETE FROM $TABLE_NAME WHERE $COLUMN_ID = :listId")
    suspend fun delete(listId: Int)
}
