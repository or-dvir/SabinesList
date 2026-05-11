package com.hotmail.or_dvir.sabinesList.ui

import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hotmail.or_dvir.sabinesList.ui.BaseScreenModel.UserEvent.SearchActiveStateChanged
import com.hotmail.or_dvir.sabinesList.ui.BaseScreenModel.UserEvent.SearchQueryChanged
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseScreenModel : ScreenModel {

    //since we are observing the DB directly, we dont have the "prompt" to start the loading state.
    //so instead we initialize to true and set to false when we get our first result
    private var _isLoading = MutableStateFlow(true)
    internal var isLoading = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    internal val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    internal val isSearchActive = _isSearchActive.asStateFlow()

    private val _sideEffectsChannel = Channel<SideEffect>(Channel.BUFFERED)
    internal val sideEffects = _sideEffectsChannel.receiveAsFlow()

    @CallSuper
    internal open fun onUserEvent(event: UserEvent) {
        when(event) {
            is SearchQueryChanged -> setSearchQuery(event.query)
            is SearchActiveStateChanged -> setSearchActiveState(event.isActive)
        }
    }

    internal fun sendSideEffect(sideEffect: SideEffect) {
        screenModelScope.launch { _sideEffectsChannel.send(sideEffect) }
    }

    internal sealed class SideEffect {
        data class ShowMessage(@StringRes val messageRes: Int) : SideEffect()
    }

    internal interface UserEvent {
        data class SearchQueryChanged(val query: String) : UserEvent
        data class SearchActiveStateChanged(val isActive: Boolean) : UserEvent
    }

    protected fun setLoadingState(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    protected fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    protected fun setSearchActiveState(isActive: Boolean) {
        _isSearchActive.value = isActive
    }
}
