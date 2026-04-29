package com.hotmail.or_dvir.sabinesList.ui.userListsScreen

import cafe.adriel.voyager.core.model.screenModelScope
import com.hotmail.or_dvir.sabinesList.R
import com.hotmail.or_dvir.sabinesList.database.repositories.UserListsRepository
import com.hotmail.or_dvir.sabinesList.models.UserList
import com.hotmail.or_dvir.sabinesList.ui.BaseScreenModel
import com.hotmail.or_dvir.sabinesList.ui.BaseScreenModel.SharedUserEvent.SearchActiveStateChanged
import com.hotmail.or_dvir.sabinesList.ui.BaseScreenModel.SharedUserEvent.SearchQueryChanged
import com.hotmail.or_dvir.sabinesList.ui.userListsScreen.UserListsScreenModel.UserEvent.CreateNewList
import com.hotmail.or_dvir.sabinesList.ui.userListsScreen.UserListsScreenModel.UserEvent.DeleteList
import com.hotmail.or_dvir.sabinesList.ui.userListsScreen.UserListsScreenModel.UserEvent.RenameList
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
        val listToDisplay = when {
            !isSearchActive -> userLists
            searchQuery.isBlank() -> emptyList()
            //search is active and query is not blank
            else -> userLists.filter { it.name.contains(searchQuery.trim(), true) }
        }

        setLoadingState(false)
        listToDisplay
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onUserEvent(userEvent: SharedUserEvent) {
        when (userEvent) {
            is CreateNewList -> onCreateNewList(userEvent.name)
            is RenameList -> onRenameList(userEvent)
            is DeleteList -> onDeleteList(userEvent.id)
            is SearchQueryChanged -> setSearchQuery(userEvent.query)
            is SearchActiveStateChanged -> setSearchActiveState(userEvent.isActive)
            else -> { /* handled by UI */ }
        }
    }

    private fun onRenameList(userEvent: RenameList) {
        screenModelScope.launch {
            userListsRepo.update(
                UserList(
                    name = userEvent.newName,
                    id = userEvent.id
                )
            )
        }
    }

    private fun onCreateNewList(name: String) {
        screenModelScope.launch {
            userListsRepo.insertOrReplace(
                UserList(name = name)
            )
            sendSideEffect(SideEffect.ShowMessage(R.string.listAdded))
        }
    }

    private fun onDeleteList(listId: Int) = screenModelScope.launch { userListsRepo.delete(listId) }

    sealed class UserEvent : SharedUserEvent {
        data class CreateNewList(val name: String) : UserEvent()
        data class RenameList(val id: Int, val newName: String) : UserEvent()
        data class DeleteList(val id: Int) : UserEvent()

        // UI-only events
        data class ListClicked(val userList: UserList) : UserEvent()
    }
}
