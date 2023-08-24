package com.hotmail.or_dvir.sabinesList.ui

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseScreenModel : ScreenModel {

    //since we are observing the DB directly, we dont have the "prompt" to start the loading state.
    //so instead we initialize to true and set to false when we get our first result
    private var _isLoadingFlow = MutableStateFlow(true)
    var isLoadingFlow = _isLoadingFlow.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQueryFlow: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActiveFlow = _isSearchActive.asStateFlow()

    fun setLoadingState(isLoading: Boolean) {
        _isLoadingFlow.value = isLoading
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query.trim()
    }

    fun setSearchActiveState(isActive: Boolean) {
        _isSearchActive.value = isActive
    }
}
