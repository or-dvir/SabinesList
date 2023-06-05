package com.hotmail.or_dvir.sabinesList.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hotmail.or_dvir.sabinesList.database.entities.ListItemEntity
import com.hotmail.or_dvir.sabinesList.database.entities.ListItemEntity.Companion.COLUMN_LIST_ID
import com.hotmail.or_dvir.sabinesList.database.entities.ListItemEntity.Companion.COLUMN_ID
import com.hotmail.or_dvir.sabinesList.database.entities.ListItemEntity.Companion.TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface ListItemDao {
    @Query("SELECT * FROM $TABLE_NAME WHERE $COLUMN_LIST_ID = :itemId")
    fun getAll(itemId: Int): Flow<List<ListItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(listItem: ListItemEntity): Long

    @Query("DELETE FROM $TABLE_NAME WHERE $COLUMN_ID = :itemId")
    suspend fun delete(itemId: Int)
}
