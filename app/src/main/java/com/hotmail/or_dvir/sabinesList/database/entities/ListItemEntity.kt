package com.hotmail.or_dvir.sabinesList.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(ListItemEntity.COLUMN_LIST_ID)],
    tableName = ListItemEntity.TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = UserListEntity::class,
        parentColumns = [UserListEntity.COLUMN_ID],
        childColumns = [ListItemEntity.COLUMN_LIST_ID],
        onDelete = CASCADE
    )]
)
data class ListItemEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Int,
    @ColumnInfo(name = COLUMN_NAME)
    val name: String,
    @ColumnInfo(name = COLUMN_LIST_ID)
    val listId: Int,
    @ColumnInfo(name = COLUMN_IS_CHECKED)
    val isChecked: Boolean
) {
    companion object {
        const val TABLE_NAME = "ListItems"
        const val COLUMN_IS_CHECKED = "isChecked"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_LIST_ID = "listId"
    }
}
