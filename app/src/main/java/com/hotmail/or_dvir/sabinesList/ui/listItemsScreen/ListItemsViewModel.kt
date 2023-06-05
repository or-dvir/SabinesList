package com.hotmail.or_dvir.sabinesList.ui.listItemsScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.hotmail.or_dvir.sabinesList.database.repositories.ListItemsRepository
import com.hotmail.or_dvir.sabinesList.models.ListItem
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsViewModel.UserEvent.OnDeleteItem
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsViewModel.UserEvent.OnNewOrEditItem
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
            is OnNewOrEditItem -> onNewOrEditListItem(userEvent.item)
            is OnDeleteItem -> onDeleteListItem(userEvent.itemId)
        }
    }

    private fun onNewOrEditListItem(item: ListItem) =
        coroutineScope.launch { repo.insertOrReplace(item) }

    private fun onDeleteListItem(itemId: Int) =
        coroutineScope.launch { repo.delete(itemId) }

    @dagger.assisted.AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(eventId: Int): ListItemsViewModel
    }

    sealed class UserEvent {
        data class OnNewOrEditItem(val item: ListItem) : UserEvent()
        data class OnDeleteItem(val itemId: Int) : UserEvent()
    }
}
