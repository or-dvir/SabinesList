package com.hotmail.or_dvir.sabinesList

import com.hotmail.or_dvir.sabinesList.database.entities.ListItemEntity
import com.hotmail.or_dvir.sabinesList.database.entities.UserListEntity
import com.hotmail.or_dvir.sabinesList.models.ListItem
import com.hotmail.or_dvir.sabinesList.models.UserList

fun UserList.toEntity() = UserListEntity(
    id = id,
    name = name.trim()
)

fun List<UserListEntity>.toUserLists() = this.map { it.toUserList() }
fun UserListEntity.toUserList() = UserList(
    id = id,
    name = name.trim()
)

fun ListItem.toEntity() = ListItemEntity(
    id = id,
    listId = listId,
    name = name.trim(),
    isChecked = isChecked
)

fun List<ListItemEntity>.toListItems() = this.map { it.toListItem() }
fun ListItemEntity.toListItem() = ListItem(
    id = id,
    name = name.trim(),
    listId = listId,
    isChecked = isChecked
)
