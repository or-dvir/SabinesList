package com.hotmail.or_dvir.sabinesList.database.firestoreDocuments

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

internal data class ListItemDocument(
    // no need for @PropertyName here because
    //  this will create an `id` field inside the firestore
    //  document, which we don't want. with firestore, the `id` is simply
    //  the path to the document
    @DocumentId
    val id: String = "",
    @PropertyName(PROPERTY_NAME)
    val name: String = "",
    @PropertyName(PROPERTY_IS_CHECKED)
    val isChecked: Boolean = false
) {
    internal companion object {
        const val PROPERTY_NAME = "name"
        const val PROPERTY_IS_CHECKED = "isChecked"
    }
}
