package com.hotmail.or_dvir.sabinesList.ui.userLists

import android.util.Log
import cafe.adriel.voyager.core.model.coroutineScope
import com.hotmail.or_dvir.sabinesList.database.repositories.UserListsRepository
import com.hotmail.or_dvir.sabinesList.models.UserList
import com.hotmail.or_dvir.sabinesList.ui.BaseScreenModel
import com.hotmail.or_dvir.sabinesList.ui.userLists.UserListsScreenModel.UserEvent.OnCreateNewList
import com.hotmail.or_dvir.sabinesList.ui.userLists.UserListsScreenModel.UserEvent.OnDeleteList
import com.hotmail.or_dvir.sabinesList.ui.userLists.UserListsScreenModel.UserEvent.OnRenameList
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserListsScreenModel @Inject constructor(
    private val userListsRepo: UserListsRepository
) : BaseScreenModel() {

    private val _userListsFlow = userListsRepo.getAllSortedByAlphabet()
    val usersListsFlow: StateFlow<List<UserList>> = combine(
        searchQueryFlow,
        _userListsFlow,
        isSearchActiveFlow
    ) { searchQuery, userLists, isSearchActive ->
        Log.i("aaaaa", "hit combined")
        val listToDisplay = when {
            !isSearchActive -> userLists
            searchQuery.isBlank() -> emptyList()
            //search is active and query is not blank
            else -> userLists.filter { it.name.contains(searchQuery) }
        }

        Log.i("aaaaa", "trying to set to false")
        setLoadingState(false)
        listToDisplay
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onUserEvent(userEvent: UserEvent) {
        when (userEvent) {
            is OnCreateNewList -> onCreateNewList(userEvent.listName)
            is OnRenameList -> onRenameList(userEvent)
            is OnDeleteList -> onDeleteList(userEvent.listId)
        }
    }

    private fun onRenameList(userEvent: OnRenameList) {
        coroutineScope.launch {
            userListsRepo.insertOrReplace(
                UserList(
                    name = userEvent.newName,
                    id = userEvent.listId
                )
            )
        }
    }

    private fun onCreateNewList(name: String) {
        coroutineScope.launch {
            userListsRepo.insertOrReplace(
                UserList(name = name)
            )
        }
    }

    private fun onDeleteList(listId: Int) = coroutineScope.launch { userListsRepo.delete(listId) }

    sealed class UserEvent {
        data class OnCreateNewList(val listName: String) : UserEvent()
        data class OnRenameList(val listId: Int, val newName: String) : UserEvent()
        data class OnDeleteList(val listId: Int) : UserEvent()
    }
}
