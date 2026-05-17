package com.hotmail.or_dvir.sabinesList.database.firestoreDocuments

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

internal data class ListItemDocument(
    @DocumentId
    @PropertyName(PROPERTY_ID)
    val id: String = "",
    @PropertyName(PROPERTY_NAME)
    val name: String = "",
    @PropertyName(PROPERTY_IS_CHECKED)
    val isChecked: Boolean = false
) {
    internal companion object {
        const val PROPERTY_ID = "id"
        const val PROPERTY_NAME = "name"
        const val PROPERTY_IS_CHECKED = "isChecked"
    }
}
