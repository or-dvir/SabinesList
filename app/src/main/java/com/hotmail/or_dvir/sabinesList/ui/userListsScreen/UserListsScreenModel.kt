package com.hotmail.or_dvir.sabinesList.ui.userListsScreen

import cafe.adriel.voyager.core.model.screenModelScope
import com.hotmail.or_dvir.sabinesList.R
import com.hotmail.or_dvir.sabinesList.database.repositories.UserListsRepository
import com.hotmail.or_dvir.sabinesList.models.UserList
import com.hotmail.or_dvir.sabinesList.ui.BaseScreenModel
import com.hotmail.or_dvir.sabinesList.ui.userListsScreen.UserListsScreenModel.UserListsEvent.CreateNewList
import com.hotmail.or_dvir.sabinesList.ui.userListsScreen.UserListsScreenModel.UserListsEvent.DeleteList
import com.hotmail.or_dvir.sabinesList.ui.userListsScreen.UserListsScreenModel.UserListsEvent.RenameList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class UserListsScreenModel @Inject constructor(
    private val userListsRepo: UserListsRepository
) : BaseScreenModel() {

    private val _userLists = userListsRepo.getAllSortedByAlphabet()
    val canSearch = _userLists
        .map { it.isNotEmpty() }
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val usersLists: StateFlow<List<UserList>> = combine(
        searchQuery,
        _userLists,
        isSearchActive
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

    override fun onUserEvent(event: UserEvent) {
        when (event) {
            is CreateNewList -> onCreateNewList(event.name)
            is RenameList -> onRenameList(event)
            is DeleteList -> onDeleteList(event.id)
            else -> super.onUserEvent(event)
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

    sealed class UserListsEvent: UserEvent {
        data class CreateNewList(val name: String) : UserListsEvent()
        data class RenameList(val id: Int, val newName: String) : UserListsEvent()
        data class DeleteList(val id: Int) : UserListsEvent()

        // UI-only events
        data class ListClicked(val userList: UserList) : UserListsEvent()
    }
}
