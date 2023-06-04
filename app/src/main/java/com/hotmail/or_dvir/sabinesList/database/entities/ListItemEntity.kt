package com.hotmail.or_dvir.sabinesList.database.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = ListItemEntity.TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = UserListEntity::class,
        parentColumns = [UserListEntity.COLUMN_ID],
        childColumns = [ListItemEntity.COLUMN_ITEM_ID],
        onDelete = CASCADE
    )]
)
data class ListItemEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Int,
    @Embedded(prefix = "start_")
    val startDate: ListItemDate,
    @Embedded(prefix = "start_")
    val startTime: ListItemTime?,
    @Embedded(prefix = "end_")
    val endDate: ListItemDate?,
    @Embedded(prefix = "end_")
    val endTime: ListItemTime?,
    @ColumnInfo(name = "note")
    val note: String,
    @ColumnInfo(name = COLUMN_ITEM_ID)
    val listId: Int
) {
    companion object {
        const val TABLE_NAME = "ListItems"
        const val COLUMN_ID = "id"
        const val COLUMN_ITEM_ID = "itemId"
    }
}

data class ListItemDate(
    @ColumnInfo(name = "year")
    val year: Int,
    @ColumnInfo(name = "month")
    val month: String,
    @ColumnInfo(name = "dayOfMonth")
    val dayOfMonth: Int
)

data class ListItemTime(
    @ColumnInfo(name = "hourOfDay")
    val hourOfDay: Int,
    @ColumnInfo(name = "minute")
    val minute: Int,
)