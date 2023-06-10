package com.hotmail.or_dvir.sabinesList.ui.listItemsScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.hotmail.or_dvir.sabinesList.database.repositories.ListItemsRepository
import com.hotmail.or_dvir.sabinesList.models.ListItem
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsViewModel.UserEvent.OnChangeItemCheckedState
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsViewModel.UserEvent.OnCreateNewItem
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsViewModel.UserEvent.OnDeleteItem
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsViewModel.UserEvent.OnRenameItem
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class ListItemsViewModel @AssistedInject constructor(
    @Assisted
    private val userListId: Int,
    private val repo: ListItemsRepository
) : ScreenModel {

    val listItemsFlow = repo.getAllByAlphabet(userListId)

    fun onUserEvent(userEvent: UserEvent) {
        when (userEvent) {
            is OnCreateNewItem -> OnCreateNewItem(userEvent.itemName)
            is OnDeleteItem -> onDeleteItem(userEvent.itemId)
            is OnRenameItem -> onRenameItem(userEvent)
            is OnChangeItemCheckedState -> onChangeItemCheckedState(userEvent)
        }
    }

    private fun onRenameItem(userEvent: OnRenameItem) =
        coroutineScope.launch {
            userEvent.apply {
                repo.rename(itemId, itemName)
            }
        }

    private fun onChangeItemCheckedState(userEvent: OnChangeItemCheckedState) =
        coroutineScope.launch {
            userEvent.apply {
                repo.changeCheckedState(itemId, isChecked)
            }
        }

    private fun onDeleteItem(itemId: Int) =
        coroutineScope.launch { repo.delete(itemId) }

    private fun OnCreateNewItem(itemName: String) =
        coroutineScope.launch {
            repo.insertOrReplace(
                ListItem(
                    name = itemName,
                    listId = userListId,
                    isChecked = false,
                )
            )
        }

    @dagger.assisted.AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(eventId: Int): ListItemsViewModel
    }

    sealed class UserEvent {
        data class OnCreateNewItem(val itemName: String) : UserEvent()
        data class OnRenameItem(val itemId: Int, val itemName: String) : UserEvent()
        data class OnChangeItemCheckedState(val itemId: Int, val isChecked: Boolean) : UserEvent()
        data class OnDeleteItem(val itemId: Int) : UserEvent()
    }
}
