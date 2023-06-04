package com.hotmail.or_dvir.sabinesList.ui.listItemsScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.hotmail.or_dvir.sabinesList.database.repositories.ListItemsRepository
import com.hotmail.or_dvir.sabinesList.models.ListItem
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsViewModel.UserEvent.OnDeleteListItem
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsViewModel.UserEvent.OnNewOrEditListItem
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class ListItemsViewModel @AssistedInject constructor(
    @Assisted
    private val userListId: Int,
    private val repo: ListItemsRepository
) : ScreenModel {

    // todo
    //  add sticky header to list for each year/month?

    val listItemsFlow = repo.getAllByStartDateDesc(userListId)

    fun onUserEvent(userEvent: UserEvent) {
        when (userEvent) {
            is OnNewOrEditListItem -> onNewOrEditListItem(userEvent.item)
            is OnDeleteListItem -> onDeleteListItem(userEvent.itemId)
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
        data class OnNewOrEditListItem(val item: ListItem) : UserEvent()
        data class OnDeleteListItem(val itemId: Int) : UserEvent()
    }
}
