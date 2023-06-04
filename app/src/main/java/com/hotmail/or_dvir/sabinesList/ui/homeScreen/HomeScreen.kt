package com.hotmail.or_dvir.sabinesList.ui.homeScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
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
import com.hotmail.or_dvir.sabinesList.ui.DeleteConfirmationDialog
import com.hotmail.or_dvir.sabinesList.ui.ErrorText
import com.hotmail.or_dvir.sabinesList.ui.SharedOverflowMenu
import com.hotmail.or_dvir.sabinesList.ui.SwipeToDeleteOrEdit
import com.hotmail.or_dvir.sabinesList.ui.SabinesListDialog
import com.hotmail.or_dvir.sabinesList.ui.collectIsDarkMode
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsScreen
import com.hotmail.or_dvir.sabinesList.ui.homeScreen.HomeScreenViewModel.UserEvent
import com.hotmail.or_dvir.sabinesList.ui.homeScreen.HomeScreenViewModel.UserEvent.OnDeleteList
import com.hotmail.or_dvir.sabinesList.ui.homeScreen.HomeScreenViewModel.UserEvent.OnEditList
import com.hotmail.or_dvir.sabinesList.ui.mainActivity.MainActivityViewModel
import com.hotmail.or_dvir.sabinesList.ui.rememberDeleteConfirmationDialogState

private typealias OnUserEvent = (event: UserEvent) -> Unit

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val mainViewModel = getViewModel<MainActivityViewModel>()
        val screenViewModel = getViewModel<HomeScreenViewModel>()
        val newListDialogState = rememberNewEditDialogState()

        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    title = { Text(stringResource(R.string.homeScreen_title)) },
                    actions = {
                        SharedOverflowMenu(
                            isDarkTheme = mainViewModel.collectIsDarkMode(),
                            onChangeTheme = { mainViewModel.setDarkMode(it) }
                        )
                    }
                )
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
                val userLists =
                    screenViewModel.userListsFlow.collectAsStateLifecycleAware(initial = emptyList()).value

                if (userLists.isEmpty()) {
                    EmptyContent()
                } else {
                    val context = LocalContext.current
                    NonEmptyContent(
                        userLists = userLists,
                        onUserEvent = { userEvent ->
                            if (userEvent is UserEvent.OnQuickOccurrenceClicked) {
                                //this is NOT a composable scope so this should NOT be a side effect
                                Toast.makeText(
                                    context,
                                    R.string.itemAdded,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            screenViewModel.onUserEvent(userEvent)
                        }
                    )
                }

                newListDialogState.apply {
                    NewEditListDialog(
                        state = this,
                        onConfirm = {
                            screenViewModel.onUserEvent(
                                UserEvent.OnCreateNewList(newListDialogState.userInput)
                            )
                        },
                        onDismiss = { reset() }
                    )
                }
            }
        }
    }

    @Composable
    private fun EmptyContent() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.homeScreen_emptyView))
        }
    }

    @Composable
    private fun NonEmptyContent(
        userLists: List<UserList>,
        onUserEvent: OnUserEvent
    ) {
        val deleteConfirmationState = rememberDeleteConfirmationDialogState()
        val editedListState = rememberNewEditDialogState()

        LazyColumn {
            itemsIndexed(
                items = userLists,
                key = { _, userList -> userList.id },
            ) { index, userList ->
                UserListRow(
                    userList = userList,
                    onUserEvent = { userEvent ->
                        when (userEvent) {
                            is OnDeleteList -> deleteConfirmationState.apply {
                                objToDeleteId = userEvent.listId
                                show = true
                            }
                            is OnEditList -> editedListState.apply {
                                userInput = userEvent.listName
                                editedListId = userEvent.listId
                                show = true
                            }
                            else -> onUserEvent(userEvent)
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

        deleteConfirmationState.apply {
            DeleteConfirmationDialog(
                state = this,
                messageRes = R.string.homeScreen_deleteConfirmation,
                onConfirm = { onUserEvent(OnDeleteList(objToDeleteId)) },
                onDismiss = { reset() }
            )
        }

        editedListState.apply {
            NewEditListDialog(
                state = this,
                onConfirm = {
                    editedListId?.let { onUserEvent(OnEditList(it, userInput)) }
                },
                onDismiss = { reset() }
            )
        }
    }

    @Composable
    private fun rememberNewEditDialogState() = remember { NewEditListDialogState() }

    @Composable
    private fun NewEditListDialog(
        state: NewEditListDialogState,
        onConfirm: () -> Unit,
        onDismiss: () -> Unit
    ) {
        if (!state.show) {
            return
        }

        val isEditing by remember(state.editedListId) {
            mutableStateOf(state.editedListId != null)
        }

        SabinesListDialog(
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
                TextField(
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
            onEditRequest = { onUserEvent(OnEditList(updatedList.id, updatedList.name)) }
        ) {
            val navigator = LocalNavigator.currentOrThrow
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .clickable { navigator.push(ListItemsScreen(updatedList)) }
                    .padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(userList.name)
                IconButton(
                    onClick = { onUserEvent(UserEvent.OnQuickOccurrenceClicked(updatedList.id)) }
                ) {
                    Icon(
                        tint = MaterialTheme.colors.secondaryVariant,
                        imageVector = Icons.Filled.AddCircle,
                        contentDescription = stringResource(R.string.contentDescription_quickInstance)
                    )
                }
            }
        }
    }
}
