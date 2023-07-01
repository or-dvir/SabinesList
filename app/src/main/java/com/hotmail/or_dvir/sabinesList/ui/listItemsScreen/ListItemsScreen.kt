package com.hotmail.or_dvir.sabinesList.ui.listItemsScreen

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.Checkbox
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.hotmail.or_dvir.sabinesList.R
import com.hotmail.or_dvir.sabinesList.collectAsStateLifecycleAware
import com.hotmail.or_dvir.sabinesList.lazyListLastItemSpacer
import com.hotmail.or_dvir.sabinesList.models.ListItem
import com.hotmail.or_dvir.sabinesList.models.UserList
import com.hotmail.or_dvir.sabinesList.ui.ErrorText
import com.hotmail.or_dvir.sabinesList.ui.NewEditNameDialogState
import com.hotmail.or_dvir.sabinesList.ui.SabinesListAlertDialog
import com.hotmail.or_dvir.sabinesList.ui.SabinesListCustomDialog
import com.hotmail.or_dvir.sabinesList.ui.SharedOverflowMenu
import com.hotmail.or_dvir.sabinesList.ui.SwipeToDeleteOrEdit
import com.hotmail.or_dvir.sabinesList.ui.collectIsDarkMode
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsViewModel.UserEvent
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsViewModel.UserEvent.OnChangeItemCheckedState
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsViewModel.UserEvent.OnCreateNewItem
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsViewModel.UserEvent.OnDeleteItem
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsViewModel.UserEvent.OnMarkAllItemsUnchecked
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsViewModel.UserEvent.OnRenameItem
import com.hotmail.or_dvir.sabinesList.ui.mainActivity.MainActivityViewModel
import com.hotmail.or_dvir.sabinesList.ui.rememberDeleteConfirmationDialogState
import com.hotmail.or_dvir.sabinesList.ui.rememberNewEditNameDialogState
import com.hotmail.or_dvir.sabinesList.ui.theme.menuIconColor

private typealias OnUserEvent = (event: UserEvent) -> Unit

data class ListItemsScreen(val list: UserList) : Screen {
    // todo
    //  change process name (fully qualified app name)
    //      first test with RELEASE flavor
    //  add "tabs" for all/completed/incompleted items in list


    @Composable
    override fun Content() {
        val mainViewModel = getViewModel<MainActivityViewModel>()
        val screenViewModel =
            getScreenModel<ListItemsViewModel, ListItemsViewModel.Factory> {
                it.create(list.id)
            }

        var showUncheckAllItemsDialog by remember { mutableStateOf(false) }
        val newItemDialogState = rememberNewEditNameDialogState()
        val navigator = LocalNavigator.current

        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    title = { Text(list.name) },
                    navigationIcon = {
                        IconButton(onClick = { navigator?.pop() }) {
                            Icon(
                                contentDescription = stringResource(R.string.contentDescription_back),
                                imageVector = Icons.Filled.ArrowBack
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showUncheckAllItemsDialog = true }) {
                            Icon(
                                tint = MaterialTheme.colors.menuIconColor,
                                painter = painterResource(R.drawable.ic_uncheck_all),
                                contentDescription = stringResource(R.string.menuItem_uncheckAll)
                            )
                        }

                        SharedOverflowMenu(
                            isDarkTheme = mainViewModel.collectIsDarkMode(),
                            onChangeTheme = { mainViewModel.setDarkMode(it) }
                        )
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { newItemDialogState.show = true }) {
                    Icon(
                        contentDescription = stringResource(R.string.contentDescription_addListItem),
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
                val listItems =
                    screenViewModel.listItemsFlow.collectAsStateLifecycleAware(initial = emptyList()).value

                if (listItems.isEmpty()) {
                    EmptyContent()
                } else {
                    NonEmptyContent(
                        listItems = listItems,
                        onUserEvent = screenViewModel::onUserEvent
                    )
                }

                newItemDialogState.apply {
                    val context = LocalContext.current
                    NewEditItemDialog(
                        state = this,
                        onConfirm = {
                            screenViewModel.onUserEvent(OnCreateNewItem(userInput))

                            //todo for now assume success
                            Toast.makeText(
                                context,
                                R.string.itemAdded,
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onDismiss = { reset() }
                    )
                }

                SabinesListAlertDialog(
                    show = showUncheckAllItemsDialog,
                    messageRes = R.string.listItemsScreen_uncheckAllConfirmation,
                    positiveButtonRes = R.string.listItemsScreen_uncheck,
                    onConfirm = {
                        screenViewModel.onUserEvent(OnMarkAllItemsUnchecked)
                        showUncheckAllItemsDialog = false
                    },
                    onDismiss = { showUncheckAllItemsDialog = false }
                )
            }
        }
    }

    @Composable
    private fun NewEditItemDialog(
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
                titleRes = if (isEditing) R.string.dialogTitle_editItem else R.string.dialogTitle_newItem,
                positiveButtonRes = if (isEditing) R.string.edit else R.string.create,
                onDismiss = onDismiss,
                positiveButtonEnabled = !isError,
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
                            Text(stringResource(R.string.hint_itemName))
                        }
                    )

                    if (state.isError) {
                        ErrorText(R.string.error_itemNameMustNotBeEmpty)
                    }
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
            Text(stringResource(R.string.listItemsScreen_emptyView))
        }
    }

    @Composable
    private fun NonEmptyContent(
        listItems: List<ListItem>,
        onUserEvent: OnUserEvent
    ) {
        val deleteItemState = rememberDeleteConfirmationDialogState()
        val editItemState = rememberNewEditNameDialogState()

        LazyColumn {
            itemsIndexed(
                items = listItems,
                key = { _, listItem -> listItem.id },
            ) { index, listItem ->
                ListItemRow(
                    listItem = listItem,
                    onUserEvent = { userEvent ->
                        when (userEvent) {
                            is OnDeleteItem -> deleteItemState.apply {
                                objToDeleteId = userEvent.itemId
                                show = true
                            }

                            is OnRenameItem -> editItemState.apply {
                                userInput = userEvent.itemName
                                editedId = userEvent.itemId
                                show = true
                            }

                            //NOT using "else" on purpose to avoid future bugs when i add functionality
                            is OnChangeItemCheckedState -> onUserEvent(userEvent)
                            is OnCreateNewItem -> onUserEvent(userEvent)
                            is OnMarkAllItemsUnchecked -> onUserEvent(userEvent)
                        }
                    }
                )

                if (index == listItems.lastIndex) {
                    Spacer(modifier = Modifier.height(lazyListLastItemSpacer))
                } else {
                    Divider()
                }
            }
        }

        deleteItemState.apply {
            SabinesListAlertDialog(
                show = show,
                messageRes = R.string.listItemsScreen_deleteConfirmation,
                positiveButtonRes = R.string.delete,
                onConfirm = { onUserEvent(OnDeleteItem(objToDeleteId)) },
                onDismiss = { reset() }
            )
        }

        editItemState.apply {
            NewEditItemDialog(
                state = this,
                onDismiss = { reset() },
                onConfirm = { editedId?.let { onUserEvent(OnRenameItem(it, userInput)) } }
            )
        }
    }

    @Composable
    private fun LazyItemScope.ListItemRow(
        listItem: ListItem,
        onUserEvent: OnUserEvent
    ) {
        val updatedItem by rememberUpdatedState(listItem)

        SwipeToDeleteOrEdit(
            onDeleteRequest = { onUserEvent(OnDeleteItem(updatedItem.id)) },
            onEditRequest = { onUserEvent(OnRenameItem(updatedItem.id, updatedItem.name)) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(listItem.name)
                Checkbox(
                    checked = listItem.isChecked,
                    onCheckedChange = {
                        onUserEvent(OnChangeItemCheckedState(updatedItem.id, it))
                    }
                )
            }
        }
    }
}
