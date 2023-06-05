package com.hotmail.or_dvir.sabinesList

import com.hotmail.or_dvir.sabinesList.database.entities.ListItemEntity
import com.hotmail.or_dvir.sabinesList.database.entities.UserListEntity
import com.hotmail.or_dvir.sabinesList.models.ListItem
import com.hotmail.or_dvir.sabinesList.models.UserList

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
    listId = listId,
    name = name
)

fun List<ListItemEntity>.toListItems() = this.map { it.toListItem() }
fun ListItemEntity.toListItem() = ListItem(
    id = id,
    name = name,
    listId = listId
)
