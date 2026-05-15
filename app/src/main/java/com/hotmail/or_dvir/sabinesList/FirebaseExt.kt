package com.hotmail.or_dvir.sabinesList

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private const val COLLECTION_LIST_ITEMS = "listItems"
private const val COLLECTION_USER_LISTS = "userLists"
private const val COLLECTION_USERS = "users"

internal fun FirebaseFirestore.userListsCollection(auth: FirebaseAuth) =
    auth.currentUser?.let { user ->
        this.collection(COLLECTION_USERS)
            .document(user.uid)
            .collection(COLLECTION_USER_LISTS)
    }

internal fun FirebaseFirestore.listsItemsCollection(auth: FirebaseAuth, listId: String) =
    userListsCollection(auth)
        ?.document(listId)
        ?.collection(COLLECTION_LIST_ITEMS)
