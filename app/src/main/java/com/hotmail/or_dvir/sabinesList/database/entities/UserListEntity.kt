package com.hotmail.or_dvir.sabinesList.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = UserListEntity.TABLE_NAME)
data class UserListEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Int,
    @ColumnInfo(name = COLUMN_NAME)
    val name: String,
) {
    companion object {
        const val TABLE_NAME = "UserLists"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
    }
}
