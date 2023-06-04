package com.hotmail.or_dvir.sabinesList

import com.hotmail.or_dvir.sabinesList.database.entities.ListItemDate
import com.hotmail.or_dvir.sabinesList.database.entities.ListItemEntity
import com.hotmail.or_dvir.sabinesList.database.entities.ListItemTime
import com.hotmail.or_dvir.sabinesList.database.entities.UserListEntity
import com.hotmail.or_dvir.sabinesList.models.ListItem
import com.hotmail.or_dvir.sabinesList.models.UserList
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month

@JvmName("UserListEntities")
fun List<UserList>.toEntities() = this.map { it.toEntity() }
fun UserList.toEntity() = UserListEntity(
    id = id,
    name = name
)

fun List<UserListEntity>.toUserLists() = this.map { it.toUserList() }
fun UserListEntity.toUserList() = UserList(
    id = id,
    name = name
)

@JvmName("ListItemEntities")
fun List<ListItem>.toEntities() = this.map { it.toEntity() }
fun ListItem.toEntity() = ListItemEntity(
    id = id,
    note = note,
    listId = listId,
    startDate = ListItemDate(
        year = startDate.year,
        month = startDate.month.name,
        dayOfMonth = startDate.dayOfMonth
    ),
    startTime = startTime?.let {
        ListItemTime(
            hourOfDay = it.hour,
            minute = it.minute
        )
    },
    endDate = endDate?.let {
        ListItemDate(
            year = it.year,
            month = it.month.name,
            dayOfMonth = it.dayOfMonth
        )
    },
    endTime = endTime?.let {
        ListItemTime(
            hourOfDay = it.hour,
            minute = it.minute
        )
    }
)

fun List<ListItemEntity>.toListItems() = this.map { it.toListItem() }
fun ListItemEntity.toListItem() = ListItem(
    id = id,
    note = note,
    listId = listId,
    startDate = LocalDate.of(
        startDate.year,
        Month.valueOf(startDate.month),
        startDate.dayOfMonth,
    ),
    startTime = startTime?.let {
        LocalTime.of(it.hourOfDay, it.minute)
    },
    endDate = endDate?.let {
        LocalDate.of(
            it.year,
            Month.valueOf(it.month),
            it.dayOfMonth,
        )
    },
    endTime = endTime?.let {
        LocalTime.of(it.hourOfDay, it.minute)
    }
)
