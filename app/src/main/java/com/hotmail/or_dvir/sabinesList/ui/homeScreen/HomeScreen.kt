package com.hotmail.or_dvir.sabinesList.ui.homeScreen

import android.widget.Toast
import androidx.annotation.StringRes
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hotmail.or_dvir.sabinesList.R
import com.hotmail.or_dvir.sabinesList.collectAsStateLifecycleAware
import com.hotmail.or_dvir.sabinesList.lazyListLastItemSpacer
import com.hotmail.or_dvir.sabinesList.models.UserList
import com.hotmail.or_dvir.sabinesList.ui.ErrorText
import com.hotmail.or_dvir.sabinesList.ui.NewEditNameDialogState
import com.hotmail.or_dvir.sabinesList.ui.SabinesListAlertDialog
import com.hotmail.or_dvir.sabinesList.ui.SabinesListCustomDialog
import com.hotmail.or_dvir.sabinesList.ui.SearchTopAppBar
import com.hotmail.or_dvir.sabinesList.ui.SharedOverflowMenu
import com.hotmail.or_dvir.sabinesList.ui.SwipeToDeleteOrEdit
import com.hotmail.or_dvir.sabinesList.ui.collectIsDarkMode
import com.hotmail.or_dvir.sabinesList.ui.homeScreen.HomeScreenViewModel.UserEvent
import com.hotmail.or_dvir.sabinesList.ui.homeScreen.HomeScreenViewModel.UserEvent.OnDeleteList
import com.hotmail.or_dvir.sabinesList.ui.homeScreen.HomeScreenViewModel.UserEvent.OnRenameList
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsScreen
import com.hotmail.or_dvir.sabinesList.ui.mainActivity.MainActivityViewModel
import com.hotmail.or_dvir.sabinesList.ui.rememberDeleteConfirmationDialogState
import com.hotmail.or_dvir.sabinesList.ui.rememberNewEditNameDialogState

private typealias OnUserEvent = (event: UserEvent) -> Unit

class HomeScreen : Screen {
    //todo
    //  add "add another" button for "new list dialog"
    //      do same for adding list items!!!

    @Composable
    override fun Content() {
        val mainViewModel = getViewModel<MainActivityViewModel>()
        val screenViewModel = getViewModel<HomeScreenViewModel>()
        val newListDialogState = rememberNewEditNameDialogState()

        val isSearchActive =
            screenViewModel.isSearchActiveFlow.collectAsStateLifecycleAware(false).value
        val searchQuery =
            screenViewModel.searchQueryFlow.collectAsStateLifecycleAware("").value

        Scaffold(
            topBar = {
                if (isSearchActive) {
                    screenViewModel.apply {
                        SearchTopAppBar(
                            searchQuery = searchQuery,
                            onSearchQueryChanged = { screenViewModel.setSearchQuery(it) },
                            onExitSearch = { screenViewModel.setSearchActiveState(false) }
                        )
                    }
                } else {
                    TopAppBar(
                        modifier = Modifier.fillMaxWidth(),
                        title = { Text(stringResource(R.string.homeScreen_title)) },
                        actions = {
                            SharedOverflowMenu(
                                isDarkTheme = mainViewModel.collectIsDarkMode(),
                                onChangeTheme = { mainViewModel.setDarkMode(it) },
                                extraAction = { /*no extra action here*/ },
                                onSearchClicked = { screenViewModel.setSearchActiveState(true) }
                            )
                        }
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { newListDialogState.show = true }) {
                    Icon(
                        contentDescription = stringResource(R.string.contentDescription_addUserList),
                        imageVector = Icons.Filled.Add
                    )
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                val userLists = screenViewModel
                    .usersListsFlow
                    .collectAsStateLifecycleAware(emptyList())
                    .value

                when {
                    userLists.isEmpty() && !isSearchActive -> EmptyContent(
                        textRes = R.string.homeScreen_emptyView
                    )

                    userLists.isEmpty() && isSearchActive -> EmptyContent(
                        textRes = R.string.homeScreen_noSearchResults,
                        contentAlignment = Alignment.TopCenter
                    )

                    else -> NonEmptyContent(
                        userLists = userLists,
                        onUserEvent = { screenViewModel.onUserEvent(it) }
                    )
                }

                newListDialogState.apply {
                    val context = LocalContext.current
                    NewEditListDialog(
                        state = this,
                        onConfirm = {
                            screenViewModel.onUserEvent(
                                UserEvent.OnCreateNewList(userInput)
                            )
                            //todo for now assume success
                            Toast.makeText(
                                context,
                                R.string.listAdded,
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onDismiss = { reset() }
                    )
                }
            }
        }
    }

    @Composable
    private fun EmptyContent(
        @StringRes textRes: Int,
        contentAlignment: Alignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = contentAlignment
        ) {
            Text(stringResource(textRes))
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
                    onUserEvent = { userEvent ->
                        when (userEvent) {
                            is OnDeleteList -> deleteListState.apply {
                                objToDeleteId = userEvent.listId
                                show = true
                            }

                            is OnRenameList -> editedListState.apply {
                                userInput = userEvent.newName
                                editedId = userEvent.listId
                                show = true
                            }

                            is UserEvent.OnCreateNewList -> onUserEvent(userEvent)
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
                onConfirm = { onUserEvent(OnDeleteList(objToDeleteId)) },
                onDismiss = { reset() }
            )
        }

        editedListState.apply {
            NewEditListDialog(
                state = this,
                onDismiss = { reset() },
                onConfirm = {
                    editedId?.let { onUserEvent(OnRenameList(it, userInput)) }
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

        SabinesListCustomDialog(
            titleRes = if (isEditing) R.string.dialogTitle_editUserList else R.string.dialogTitle_newUserList,
            positiveButtonRes = if (isEditing) R.string.edit else R.string.create,
            positiveButtonEnabled = !state.isError,
            onDismiss = onDismiss,
            onPositiveButtonClick = {
                onConfirm()
                onDismiss()
            }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                //todo make this take focus
                //  warning: creates a chain reaction of changes...
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.userInput,
                    onValueChange = { state.userInput = it },
                    placeholder = {
                        Text(stringResource(R.string.hint_listName))
                    }
                )

                if (state.isError) {
                    ErrorText(R.string.error_listNameMustNotBeEmpty)
                }
            }
        }
    }

    @Composable
    private fun LazyItemScope.UserListRow(
        userList: UserList,
        onUserEvent: OnUserEvent
    ) {
        val updatedList by rememberUpdatedState(userList)

        SwipeToDeleteOrEdit(
            onDeleteRequest = { onUserEvent(OnDeleteList(updatedList.id)) },
            onEditRequest = { onUserEvent(OnRenameList(updatedList.id, updatedList.name)) }
        ) {
            val navigator = LocalNavigator.currentOrThrow
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .clickable { navigator.push(ListItemsScreen(updatedList)) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(userList.name)
            }
        }
    }
}
