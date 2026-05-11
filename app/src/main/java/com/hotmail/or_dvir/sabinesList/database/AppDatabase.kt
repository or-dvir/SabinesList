package com.hotmail.or_dvir.sabinesList.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hotmail.or_dvir.sabinesList.database.daos.ListItemDao
import com.hotmail.or_dvir.sabinesList.database.daos.UserListDao
import com.hotmail.or_dvir.sabinesList.database.entities.ListItemEntity
import com.hotmail.or_dvir.sabinesList.database.entities.UserListEntity

@Database(
    entities = [
        UserListEntity::class,
        ListItemEntity::class
    ],
    version = 1
)
internal abstract class AppDatabase : RoomDatabase() {
    internal abstract fun userListsDao(): UserListDao
    internal abstract fun listItemsDao(): ListItemDao
}
