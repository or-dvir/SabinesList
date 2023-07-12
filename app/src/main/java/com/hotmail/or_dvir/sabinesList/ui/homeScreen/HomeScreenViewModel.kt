package com.hotmail.or_dvir.sabinesList.ui.homeScreen

import androidx.lifecycle.viewModelScope
import com.hotmail.or_dvir.sabinesList.database.repositories.UserListsRepository
import com.hotmail.or_dvir.sabinesList.models.UserList
import com.hotmail.or_dvir.sabinesList.ui.SearchViewModel
import com.hotmail.or_dvir.sabinesList.ui.homeScreen.HomeScreenViewModel.UserEvent.OnCreateNewList
import com.hotmail.or_dvir.sabinesList.ui.homeScreen.HomeScreenViewModel.UserEvent.OnDeleteList
import com.hotmail.or_dvir.sabinesList.ui.homeScreen.HomeScreenViewModel.UserEvent.OnRenameList
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val userListsRepo: UserListsRepository
) : SearchViewModel() {

    can probably create a functoin in the base view model that does most of this...
    private val _userListsFlow = userListsRepo.getAllSortedByAlphabet()
    val usersListsFlow: StateFlow<List<UserList>> = combine(
        searchQueryFlow,
        _userListsFlow,
        isSearchActiveFlow
    ) { searchQuery, userLists, isSearchActive ->
        when {
            !isSearchActive -> userLists
            searchQuery.isBlank() -> emptyList()
            //search is active and query is not blank
            else -> userLists.filter { it.name.contains(searchQuery) }
        }
    }.stateIn(
        scope = viewModelScope,
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
        viewModelScope.launch {
            userListsRepo.insertOrReplace(
                UserList(
                    name = userEvent.newName,
                    id = userEvent.listId
                )
            )
        }
    }

    private fun onCreateNewList(name: String) {
        viewModelScope.launch {
            userListsRepo.insertOrReplace(
                UserList(name = name)
            )
        }
    }

    private fun onDeleteList(listId: Int) = viewModelScope.launch { userListsRepo.delete(listId) }

    sealed class UserEvent {
        data class OnCreateNewList(val listName: String) : UserEvent()
        data class OnRenameList(val listId: Int, val newName: String) : UserEvent()
        data class OnDeleteList(val listId: Int) : UserEvent()
    }
}
