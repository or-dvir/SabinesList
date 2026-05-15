package com.hotmail.or_dvir.sabinesList.database.firestoreDocuments

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

internal data class ListItemDocument(
    @DocumentId
    @PropertyName("id")
    val id: String = "",
    @PropertyName("name")
    val name: String = "",
    @PropertyName("isChecked")
    val isChecked: Boolean = false
)
