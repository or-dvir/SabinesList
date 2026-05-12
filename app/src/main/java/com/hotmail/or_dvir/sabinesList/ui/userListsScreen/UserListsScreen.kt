package com.hotmail.or_dvir.sabinesList.ui.userListsScreen

import android.widget.Button
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hotmail.or_dvir.sabinesList.R
import com.hotmail.or_dvir.sabinesList.collectAsStateLifecycleAware
import com.hotmail.or_dvir.sabinesList.lazyListLastItemSpacer
import com.hotmail.or_dvir.sabinesList.models.UserList
import com.hotmail.or_dvir.sabinesList.ui.BaseScreenModel.UserEvent
import com.hotmail.or_dvir.sabinesList.ui.BaseScreenModel.UserEvent.SearchActiveStateChanged
import com.hotmail.or_dvir.sabinesList.ui.BaseScreenModel.UserEvent.SearchQueryChanged
import com.hotmail.or_dvir.sabinesList.ui.BaseScreenModel.SideEffect
import com.hotmail.or_dvir.sabinesList.ui.EmptyContent
import com.hotmail.or_dvir.sabinesList.ui.ErrorText
import com.hotmail.or_dvir.sabinesList.ui.LoadingContent
import com.hotmail.or_dvir.sabinesList.ui.MenuItemUiState.Preferences
import com.hotmail.or_dvir.sabinesList.ui.MenuItemUiState.Search
import com.hotmail.or_dvir.sabinesList.ui.MenuItemUiState.Share
import com.hotmail.or_dvir.sabinesList.ui.MenuItemUiState.UncheckAll
import com.hotmail.or_dvir.sabinesList.ui.NewEditNameDialogState
import com.hotmail.or_dvir.sabinesList.ui.OnMenuItemClicked
import com.hotmail.or_dvir.sabinesList.ui.SabinesListAlertDialog
import com.hotmail.or_dvir.sabinesList.ui.SabinesListCustomDialog
import com.hotmail.or_dvir.sabinesList.ui.SearchTopAppBar
import com.hotmail.or_dvir.sabinesList.ui.SwipeToDeleteOrEdit
import com.hotmail.or_dvir.sabinesList.ui.TopAppBarActions
import com.hotmail.or_dvir.sabinesList.ui.TopAppBarTitle
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsScreen
import com.hotmail.or_dvir.sabinesList.ui.preferences.PreferencesScreen
import com.hotmail.or_dvir.sabinesList.ui.rememberDeleteConfirmationDialogState
import com.hotmail.or_dvir.sabinesList.ui.rememberNewEditNameDialogState
import com.hotmail.or_dvir.sabinesList.ui.theme.fabContentColor
import com.hotmail.or_dvir.sabinesList.ui.userListsScreen.UserListsScreenModel.UserListsEvent.CreateNewList
import com.hotmail.or_dvir.sabinesList.ui.userListsScreen.UserListsScreenModel.UserListsEvent.DeleteList
import com.hotmail.or_dvir.sabinesList.ui.userListsScreen.UserListsScreenModel.UserListsEvent.ListClicked
import com.hotmail.or_dvir.sabinesList.ui.userListsScreen.UserListsScreenModel.UserListsEvent.RenameList
import kotlinx.coroutines.flow.collectLatest

private typealias OnUserEvent = (event: UserEvent) -> Unit

internal class UserListsScreen : Screen {
    // todo add feature to mark list as "Favorite"
    //      either they always appear on top, or add bottom bar with "all/favorites" tabs

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<UserListsScreenModel>()
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        val userLists by screenModel.usersLists.collectAsStateLifecycleAware(emptyList())
        val canSearch by screenModel.canSearch.collectAsStateLifecycleAware(false)
        val isLoading by screenModel.isLoading.collectAsStateLifecycleAware(true)
        val isSearchActive by screenModel.isSearchActive.collectAsStateLifecycleAware(false)
        val searchQuery by screenModel.searchQuery.collectAsStateLifecycleAware("")

        val newListDialogState = rememberNewEditNameDialogState()

        LaunchedEffect(Unit) {
            screenModel.sideEffects.collectLatest { sideEffect ->
                when (sideEffect) {
                    is SideEffect.ShowMessage -> Toast.makeText(
                        context,
                        sideEffect.messageRes,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        val onUserEvent: OnUserEvent = { event ->
            when (event) {
                is ListClicked -> navigator.push(ListItemsScreen(event.userList))
                else -> screenModel.onUserEvent(event)
            }
        }

        val onMenuItemClicked: OnMenuItemClicked = { item ->
            when (item) {
                is Preferences -> navigator.push(PreferencesScreen())
                // search button can only be pressed if search "mode" is inactive
                is Search -> onUserEvent(SearchActiveStateChanged(true))
                is Share,
                is UncheckAll -> {
                    // not relevant for this screen.
                    // deliberately left empty so compilation fails if new menu items are added.
                }
            }
        }

        Scaffold(
            topBar = {
                ScreenTopAppBar(
                    canSearch = canSearch,
                    isSearchActive = isSearchActive,
                    searchQuery = searchQuery,
                    onUserEvent = onUserEvent,
                    onMenuItemClick = onMenuItemClicked
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    contentColor = MaterialTheme.colors.fabContentColor,
                    onClick = { newListDialogState.show = true }
                ) {
                    Icon(
                        contentDescription = stringResource(R.string.contentDescription_addUserList),
                        painter = painterResource(R.drawable.ic_add)
                    )
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                ScreenContent(
                    userLists = userLists,
                    isLoading = isLoading,
                    isSearchActive = isSearchActive,
                    onUserEvent = onUserEvent
                )

                newListDialogState.apply {
                    NewEditListDialog(
                        state = this,
                        onConfirm = { onUserEvent(CreateNewList(userInput)) },
                        onDismiss = { reset() }
                    )
                }
            }
        }
    }

    @Composable
    private fun ScreenContent(
        userLists: List<UserList>,
        isLoading: Boolean,
        isSearchActive: Boolean,
        onUserEvent: OnUserEvent
    ) {
        when {
            isLoading -> LoadingContent()

            userLists.isEmpty() -> {
                EmptyContent(
                    messageTextRes = if(isSearchActive) R.string.search_noResults else R.string.homeScreen_emptyView,
                    buttonTextRes = null
                )
            }

            else -> NonEmptyContent(
                userLists = userLists,
                onUserEvent = onUserEvent
            )
        }
    }

    @Composable
    private fun ScreenTopAppBar(
        canSearch: Boolean,
        isSearchActive: Boolean,
        searchQuery: String,
        onUserEvent: OnUserEvent,
        onMenuItemClick: OnMenuItemClicked
    ) {
        if (isSearchActive) {
            SearchTopAppBar(
                searchQuery = searchQuery,
                hint = stringResource(R.string.searchHint_lists),
                onSearchQueryChanged = { onUserEvent(SearchQueryChanged(it)) },
                onExitSearch = { onUserEvent(SearchActiveStateChanged(false)) }
            )
        } else {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = { TopAppBarTitle(stringResource(R.string.homeScreen_title)) },

                actions = {
                    TopAppBarActions(
                        menuItems = listOf(
                            Search(enabled = canSearch),
                            Preferences
                        ),
                        onItemClicked = onMenuItemClick
                    )
                }
            )
        }
    }

    @Composable
    private fun NonEmptyContent(
        userLists: List<UserList>,
        onUserEvent: OnUserEvent
    ) {
        val deleteListState = rememberDeleteConfirmationDialogState()
        val editedListState = rememberNewEditNameDialogState()

        LazyColumn {
            itemsIndexed(
                items = userLists,
                key = { _, userList -> userList.id },
            ) { index, userList ->
                UserListRow(
                    userList = userList,
                    onUserEvent = onUserEvent,
                    onRequestDelete = {
                        deleteListState.apply {
                            objToDeleteId = it
                            show = true
                        }
                    },
                    onRequestRename = { id, name ->
                        editedListState.apply {
                            userInput = name
                            editedId = id
                            show = true
                        }
                    }
                )

                if (index == userLists.lastIndex) {
                    Spacer(modifier = Modifier.height(lazyListLastItemSpacer))
                } else {
                    Divider()
                }
            }
        }

        deleteListState.apply {
            SabinesListAlertDialog(
                show = show,
                messageRes = R.string.homeScreen_deleteConfirmation,
                positiveButtonRes = R.string.delete,
                onConfirm = { onUserEvent(DeleteList(objToDeleteId)) },
                onDismiss = { reset() }
            )
        }

        editedListState.apply {
            NewEditListDialog(
                state = this,
                onDismiss = { reset() },
                onConfirm = {
                    editedId?.let { onUserEvent(RenameList(it, userInput)) }
                }
            )
        }
    }

    @Composable
    private fun NewEditListDialog(
        state: NewEditNameDialogState,
        onConfirm: () -> Unit,
        onDismiss: () -> Unit
    ) {
        if (!state.show) {
            return
        }

        val isEditing by remember(state.editedId) {
            mutableStateOf(state.editedId != null)
        }

        state.apply {
            SabinesListCustomDialog(
                titleRes = if (isEditing) R.string.dialogTitle_editUserList else R.string.dialogTitle_newUserList,
                onDismiss = onDismiss,
                positiveButtonRes = if (isEditing) R.string.edit else R.string.create,
                positiveButtonEnabled = !isError,
                onPositiveButtonClick = {
                    onConfirm()
                    onDismiss()
                },
                neutralButtonRes = if (isEditing) null else R.string.createAnother,
                neutralButtonEnabled = !isError,
                onNeutralButtonClicked = {
                    onConfirm()
                    userInput = ""
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    //todo make this take focus
                    //  warning: creates a chain reaction of changes...
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = userInput,
                        onValueChange = { userInput = it },
                        placeholder = {
                            Text(stringResource(R.string.hint_listName))
                        }
                    )

                    if (isError) {
                        ErrorText(R.string.error_listNameMustNotBeEmpty)
                    }
                }
            }
        }
    }

    @Composable
    private fun LazyItemScope.UserListRow(
        userList: UserList,
        onUserEvent: OnUserEvent,
        onRequestDelete: (listId: Int) -> Unit,
        onRequestRename: (listId: Int, name: String) -> Unit
    ) {
        val updatedList by rememberUpdatedState(userList)

        SwipeToDeleteOrEdit(
            onDeleteRequest = { onRequestDelete(updatedList.id) },
            onEditRequest = { onRequestRename(updatedList.id, updatedList.name) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .clickable { onUserEvent(ListClicked(updatedList)) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(userList.name)
            }
        }
    }
}
