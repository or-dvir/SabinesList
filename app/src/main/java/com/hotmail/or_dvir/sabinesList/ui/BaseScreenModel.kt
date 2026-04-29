package com.hotmail.or_dvir.sabinesList.ui

import androidx.annotation.StringRes
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseScreenModel : ScreenModel {

    //since we are observing the DB directly, we dont have the "prompt" to start the loading state.
    //so instead we initialize to true and set to false when we get our first result
    private var _isLoadingFlow = MutableStateFlow(true)
    var isLoadingFlow = _isLoadingFlow.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQueryFlow: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActiveFlow = _isSearchActive.asStateFlow()

    private val _sideEffectsChannel = Channel<SideEffect>(Channel.BUFFERED)
    val sideEffectsFlow = _sideEffectsChannel.receiveAsFlow()

    protected fun sendSideEffect(sideEffect: SideEffect) {
        screenModelScope.launch { _sideEffectsChannel.send(sideEffect) }
    }

    sealed class SideEffect {
        data class ShowMessage(@StringRes val messageRes: Int) : SideEffect()
    }

    interface SharedUserEvent {
        data class SearchQueryChanged(val query: String) : SharedUserEvent
        data class SearchActiveStateChanged(val isActive: Boolean) : SharedUserEvent
    }

    protected fun setLoadingState(isLoading: Boolean) {
        _isLoadingFlow.value = isLoading
    }

    protected fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    protected fun setSearchActiveState(isActive: Boolean) {
        _isSearchActive.value = isActive
    }
}
