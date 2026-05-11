package com.hotmail.or_dvir.sabinesList

import com.hotmail.or_dvir.sabinesList.database.entities.ListItemEntity
import com.hotmail.or_dvir.sabinesList.database.entities.UserListEntity
import com.hotmail.or_dvir.sabinesList.models.ListItem
import com.hotmail.or_dvir.sabinesList.models.UserList

internal fun UserList.toEntity() = UserListEntity(
    id = id,
    name = name.trim()
)

internal fun List<UserListEntity>.toUserLists() = this.map { it.toUserList() }
internal fun UserListEntity.toUserList() = UserList(
    id = id,
    name = name.trim()
)

internal fun ListItem.toEntity() = ListItemEntity(
    id = id,
    listId = listId,
    name = name.trim(),
    isChecked = isChecked
)

internal fun List<ListItemEntity>.toListItems() = this.map { it.toListItem() }
internal fun ListItemEntity.toListItem() = ListItem(
    id = id,
    name = name.trim(),
    listId = listId,
    isChecked = isChecked
)
