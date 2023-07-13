package com.hotmail.or_dvir.sabinesList.ui.listItemsScreen

import cafe.adriel.voyager.core.model.coroutineScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.hotmail.or_dvir.sabinesList.database.repositories.ListItemsRepository
import com.hotmail.or_dvir.sabinesList.models.ListItem
import com.hotmail.or_dvir.sabinesList.ui.SearchScreenModel
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsScreenModel.UserEvent.OnChangeItemCheckedState
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsScreenModel.UserEvent.OnCreateNewItem
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsScreenModel.UserEvent.OnDeleteItem
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsScreenModel.UserEvent.OnMarkAllItemsUnchecked
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsScreenModel.UserEvent.OnRenameItem
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ListItemsScreenModel @AssistedInject constructor(
    @Assisted
    private val userListId: Int,
    private val repo: ListItemsRepository
) : SearchScreenModel() {

    private val _listItemsFlow = repo.getAllByAlphabet(userListId)
    val listItemsFlow: StateFlow<List<ListItem>> = combine(
        searchQueryFlow,
        _listItemsFlow,
        isSearchActiveFlow
    ) { searchQuery, listItems, isSearchActive ->
        when {
            !isSearchActive -> listItems
            searchQuery.isBlank() -> emptyList()
            //search is active and query is not blank
            else -> listItems.filter { it.name.contains(searchQuery) }
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onUserEvent(userEvent: UserEvent) {
        when (userEvent) {
            is OnCreateNewItem -> onCreateNewItem(userEvent.itemName)
            is OnDeleteItem -> onDeleteItem(userEvent.itemId)
            is OnRenameItem -> onRenameItem(userEvent)
            is OnChangeItemCheckedState -> onChangeItemCheckedState(userEvent)
            is OnMarkAllItemsUnchecked -> onMarkAllUnchecked()
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

    private fun onDeleteItem(itemId: Int) = coroutineScope.launch { repo.delete(itemId) }

    private fun onMarkAllUnchecked() = coroutineScope.launch { repo.markAllUnchecked(userListId) }

    private fun onCreateNewItem(itemName: String) =
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
        fun create(eventId: Int): ListItemsScreenModel
    }

    sealed class UserEvent {
        data class OnCreateNewItem(val itemName: String) : UserEvent()
        data class OnRenameItem(val itemId: Int, val itemName: String) : UserEvent()
        data class OnChangeItemCheckedState(val itemId: Int, val isChecked: Boolean) : UserEvent()
        data class OnDeleteItem(val itemId: Int) : UserEvent()
        object OnMarkAllItemsUnchecked : UserEvent()
    }
}
