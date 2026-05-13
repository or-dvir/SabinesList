package com.hotmail.or_dvir.sabinesList.models

internal data class ListItem(
    val name: String,
    // todo only needed for backwards compatibility with Room. remove once Room is removed
    val listId: String,
    val isChecked: Boolean,
    // since we are using auto-generated id's, 0 means "no id is set. create one"
    val id: String = "0"
)
