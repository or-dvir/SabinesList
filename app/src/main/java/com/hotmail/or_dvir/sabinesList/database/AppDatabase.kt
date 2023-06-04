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
abstract class AppDatabase : RoomDatabase() {
    abstract fun userListsDao(): UserListDao
    abstract fun listItemsDao(): ListItemDao
}
