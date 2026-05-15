package com.hotmail.or_dvir.sabinesList

import com.hotmail.or_dvir.sabinesList.database.entities.ListItemEntity
import com.hotmail.or_dvir.sabinesList.database.entities.UserListEntity
import com.hotmail.or_dvir.sabinesList.database.firestoreDocuments.ListItemDocument
import com.hotmail.or_dvir.sabinesList.database.firestoreDocuments.UserListDocument
import com.hotmail.or_dvir.sabinesList.models.ListItem
import com.hotmail.or_dvir.sabinesList.models.UserList

internal fun List<UserListDocument>.toUserLists() = this.map { it.toUserList() }
internal fun UserListDocument.toUserList() = UserList(
    name = name,
    id = id
)

internal fun List<ListItemDocument>.toListItems() = this.map { it.toListItem() }
internal fun ListItemDocument.toListItem() = ListItem(
    name = name,
    listId = "irrelevant in this case as firestore does not need listId",
    isChecked = isChecked,
    id = id
)

internal fun ListItemEntity.toDocument() = ListItemDocument(
    id = id.toString(),
    name = name,
    isChecked = isChecked
)

internal fun UserListEntity.toDocument() = UserListDocument(
    id = id.toString(),
    name = name
)

internal fun UserList.toEntity() = UserListEntity(
    id = id.toInt(),
    name = name.trim()
)

internal fun List<UserListEntity>.toUserLists() = this.map { it.toUserList() }
internal fun UserListEntity.toUserList() = UserList(
    id = id.toString(),
    name = name.trim()
)

internal fun ListItem.toEntity() = ListItemEntity(
    id = id.toInt(),
    listId = listId.toInt(),
    name = name.trim(),
    isChecked = isChecked
)

internal fun List<ListItemEntity>.toListItems() = this.map { it.toListItem() }
internal fun ListItemEntity.toListItem() = ListItem(
    id = id.toString(),
    name = name.trim(),
    listId = listId.toString(),
    isChecked = isChecked
)
