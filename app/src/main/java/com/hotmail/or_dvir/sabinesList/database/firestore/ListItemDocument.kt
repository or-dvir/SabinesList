package com.hotmail.or_dvir.sabinesList.database.firestore

import com.google.firebase.firestore.DocumentId

internal data class ListItemDocument(
    @DocumentId
    val id: String,
    val name: String,
    val isChecked: Boolean
) {
    internal companion object {
        const val FIRESTORE_COLLECTION_NAME = "ListItems"
    }
}
