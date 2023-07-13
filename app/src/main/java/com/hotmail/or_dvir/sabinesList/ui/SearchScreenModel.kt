package com.hotmail.or_dvir.sabinesList.ui

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class SearchScreenModel : ScreenModel {
    private val _searchQuery = MutableStateFlow("")
    val searchQueryFlow: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActiveFlow = _isSearchActive.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSearchActiveState(isActive: Boolean) {
        _isSearchActive.value = isActive
    }
}
