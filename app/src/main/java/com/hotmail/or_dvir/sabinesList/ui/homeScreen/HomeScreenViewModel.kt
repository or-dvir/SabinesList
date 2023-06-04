package com.hotmail.or_dvir.sabinesList.ui.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotmail.or_dvir.sabinesList.database.repositories.ListItemsRepository
import com.hotmail.or_dvir.sabinesList.database.repositories.UserListsRepository
import com.hotmail.or_dvir.sabinesList.models.ListItem
import com.hotmail.or_dvir.sabinesList.models.UserList
import com.hotmail.or_dvir.sabinesList.ui.homeScreen.HomeScreenViewModel.UserEvent.OnCreateNewList
import com.hotmail.or_dvir.sabinesList.ui.homeScreen.HomeScreenViewModel.UserEvent.OnDeleteList
import com.hotmail.or_dvir.sabinesList.ui.homeScreen.HomeScreenViewModel.UserEvent.OnQuickOccurrenceClicked
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val userListsRepo: UserListsRepository,
    private val listItemssRepo: ListItemsRepository
) : ViewModel() {

    val userListsFlow = userListsRepo.getAllSortedByAlphabet()

    fun onUserEvent(userEvent: UserEvent) {
        when (userEvent) {
            is OnQuickOccurrenceClicked -> onQuickOccurrenceClicked(userEvent.listId)
            is OnCreateNewList -> onCreateNewList(userEvent.listName)
            is OnDeleteList -> onDeleteList(userEvent.listId)
            is UserEvent.OnEditList -> onEditList(userEvent)
        }
    }

    private fun onEditList(userEvent: UserEvent.OnEditList) {
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

    private fun onDeleteList(listId: Int) =
        viewModelScope.launch { userListsRepo.delete(listId) }

    private fun onQuickOccurrenceClicked(listId: Int) {
        viewModelScope.launch {
            listItemssRepo.insertOrReplace(
                ListItem(
                    note = "",
                    listId = listId,
                    startDate = LocalDate.now(),
                    startTime = LocalTime.now(),
                    endDate = null,
                    endTime = null,
                )
            )
        }
    }

    sealed class UserEvent {
        data class OnCreateNewList(val listName: String) : UserEvent()
        data class OnQuickOccurrenceClicked(val listId: Int) : UserEvent()
        data class OnDeleteList(val listId: Int) : UserEvent()
        data class OnEditList(val listId: Int, val listName: String) : UserEvent()
    }
}
