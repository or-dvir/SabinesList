package com.hotmail.or_dvir.sabinesList.ui.listItemsScreen

import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.hotmail.or_dvir.sabinesList.R
import com.hotmail.or_dvir.sabinesList.database.repositories.ListItemsRepository
import com.hotmail.or_dvir.sabinesList.models.ListItem
import com.hotmail.or_dvir.sabinesList.ui.BaseScreenModel
import com.hotmail.or_dvir.sabinesList.ui.BaseScreenModel.SharedUserEvent.SearchActiveStateChanged
import com.hotmail.or_dvir.sabinesList.ui.BaseScreenModel.SharedUserEvent.SearchQueryChanged
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsScreenModel.UserEvent.ChangeItemCheckedState
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsScreenModel.UserEvent.CreateNewItem
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsScreenModel.UserEvent.DeleteItem
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsScreenModel.UserEvent.MarkAllItemsUnchecked
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsScreenModel.UserEvent.RenameItem
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ListItemsScreenModel @AssistedInject constructor(
    @Assisted
    private val userListId: Int,
    private val repo: ListItemsRepository
) : BaseScreenModel() {

    private val _currentBottomNavigationItemFlow =
        MutableStateFlow<BottomNavigationListItem>(BottomNavigationListItem.AllItems)
    val currentBottomNavigationItemFlow = _currentBottomNavigationItemFlow.asStateFlow()

    private val _listItemsFlow = repo.getAllByAlphabet(userListId)
    val listItemsFlow: StateFlow<List<ListItem>> = combine(
        searchQueryFlow,
        _listItemsFlow,
        isSearchActiveFlow,
        _currentBottomNavigationItemFlow
    ) { searchQuery, listItems, isSearchActive, bottomNavItem ->
        val itemsToDisplay = when {
            !isSearchActive -> {
                when (bottomNavItem) {
                    BottomNavigationListItem.AllItems -> listItems
                    BottomNavigationListItem.CheckedItems -> listItems.filter { it.isChecked }
                    BottomNavigationListItem.UncheckedItems -> listItems.filterNot { it.isChecked }
                }
            }
            //if we are here, search is active
            searchQuery.isBlank() -> emptyList()
            else -> listItems.filter { it.name.contains(searchQuery.trim(), true) }
        }

        setLoadingState(false)
        itemsToDisplay
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    override fun onUserEvent(event: SharedUserEvent) {
        when (event) {
            is CreateNewItem -> onCreateNewItem(event.itemName)
            is RenameItem -> onRenameItem(event)
            is DeleteItem -> onDeleteItem(event.itemId)
            is ChangeItemCheckedState -> onChangeItemCheckedState(event)
            is MarkAllItemsUnchecked -> onMarkAllUnchecked()
            is UserEvent.BottomNavigationItemClicked ->
                _currentBottomNavigationItemFlow.value = event.item
            else -> super.onUserEvent(event)
        }
    }

    private fun onRenameItem(userEvent: RenameItem) =
        screenModelScope.launch {
            userEvent.apply {
                repo.rename(itemId, itemName)
            }
        }

    private fun onChangeItemCheckedState(userEvent: ChangeItemCheckedState) =
        screenModelScope.launch {
            userEvent.apply {
                repo.changeCheckedState(itemId, isChecked)
            }
        }

    private fun onDeleteItem(itemId: Int) = screenModelScope.launch { repo.delete(itemId) }

    private fun onMarkAllUnchecked() = screenModelScope.launch { repo.markAllUnchecked(userListId) }

    private fun onCreateNewItem(itemName: String) =
        screenModelScope.launch {
            repo.insertOrReplace(
                ListItem(
                    name = itemName,
                    listId = userListId,
                    isChecked = false,
                )
            )
            // todo for now assume success
            sendSideEffect(SideEffect.ShowMessage(R.string.itemAdded))
        }

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(eventId: Int): ListItemsScreenModel
    }

    sealed class UserEvent : SharedUserEvent {
        data class CreateNewItem(val itemName: String) : UserEvent()
        data class RenameItem(val itemId: Int, val itemName: String) : UserEvent()
        data class ChangeItemCheckedState(val itemId: Int, val isChecked: Boolean) : UserEvent()
        data class DeleteItem(val itemId: Int) : UserEvent()
        data class BottomNavigationItemClicked(val item: BottomNavigationListItem) : UserEvent()
        object MarkAllItemsUnchecked : UserEvent()
    }
}
