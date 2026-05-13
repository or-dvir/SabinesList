package com.hotmail.or_dvir.sabinesList.database.firestore

import com.google.firebase.firestore.DocumentId

internal data class UserListDocument(
    @DocumentId
    val id: String,
    val name: String
) {
    internal companion object {
        const val FIRESTORE_COLLECTION_NAME = "UserLists"
    }
}
