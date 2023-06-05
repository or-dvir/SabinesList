package com.hotmail.or_dvir.sabinesList.ui.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotmail.or_dvir.sabinesList.database.repositories.UserListsRepository
import com.hotmail.or_dvir.sabinesList.models.UserList
import com.hotmail.or_dvir.sabinesList.ui.homeScreen.HomeScreenViewModel.UserEvent.OnCreateNewList
import com.hotmail.or_dvir.sabinesList.ui.homeScreen.HomeScreenViewModel.UserEvent.OnDeleteList
import com.hotmail.or_dvir.sabinesList.ui.homeScreen.HomeScreenViewModel.UserEvent.OnEditList
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val userListsRepo: UserListsRepository
) : ViewModel() {

    val userListsFlow = userListsRepo.getAllSortedByAlphabet()

    fun onUserEvent(userEvent: UserEvent) {
        when (userEvent) {
            is OnCreateNewList -> onCreateNewList(userEvent.listName)
            is OnEditList -> onEditList(userEvent)
            is OnDeleteList -> onDeleteList(userEvent.listId)
        }
    }

    private fun onEditList(userEvent: OnEditList) {
        viewModelScope.launch {
            userListsRepo.insertOrReplace(
                UserList(
                    name = userEvent.listName,
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
        data class OnEditList(val listId: Int, val listName: String) : UserEvent()
        data class OnDeleteList(val listId: Int) : UserEvent()
    }
}
