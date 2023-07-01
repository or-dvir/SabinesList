package com.hotmail.or_dvir.sabinesList.ui.searchScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotmail.or_dvir.sabinesList.database.repositories.ListItemsRepository
import com.hotmail.or_dvir.sabinesList.database.repositories.UserListsRepository
import com.hotmail.or_dvir.sabinesList.models.ListItem
import com.hotmail.or_dvir.sabinesList.models.UserList
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val itemsRepo: ListItemsRepository,
    private val listsRepo: UserListsRepository
) : ViewModel() {

    private val _itemsResultFlow = MutableStateFlow<List<ListItem>>(emptyList())
    val itemsResultFlow = _itemsResultFlow.asStateFlow()

    private val _listsResultFlow = MutableStateFlow<List<UserList>>(emptyList())
    val listsResultFlow = _listsResultFlow.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun search(query: String) {
        if (query.isBlank()) {
            viewModelScope.launch {
                _itemsResultFlow.emit(emptyList())
                _listsResultFlow.emit(emptyList())
            }
        }

        viewModelScope.launch {
            val itemsResult = async { itemsRepo.search(query) }
            val listsResult = async { listsRepo.search(query) }

            awaitAll(itemsResult, listsResult)

            _itemsResultFlow.emit(itemsResult.getCompleted())
            _listsResultFlow.emit(listsResult.getCompleted())
        }
    }
}
