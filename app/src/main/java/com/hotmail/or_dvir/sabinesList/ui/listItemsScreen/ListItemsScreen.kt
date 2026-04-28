package com.hotmail.or_dvir.sabinesList.ui.listItemsScreen

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.hotmail.or_dvir.sabinesList.R
import com.hotmail.or_dvir.sabinesList.collectAsStateLifecycleAware
import com.hotmail.or_dvir.sabinesList.lazyListLastItemSpacer
import com.hotmail.or_dvir.sabinesList.models.ListItem
import com.hotmail.or_dvir.sabinesList.models.UserList
import com.hotmail.or_dvir.sabinesList.ui.BaseScreenModel.SharedUserEvent
import com.hotmail.or_dvir.sabinesList.ui.BaseScreenModel.SharedUserEvent.ChangeTheme
import com.hotmail.or_dvir.sabinesList.ui.BaseScreenModel.SharedUserEvent.SearchActiveStateChanged
import com.hotmail.or_dvir.sabinesList.ui.BaseScreenModel.SharedUserEvent.SearchQueryChanged
import com.hotmail.or_dvir.sabinesList.ui.BaseScreenModel.SideEffect
import com.hotmail.or_dvir.sabinesList.ui.EmptyContent
import com.hotmail.or_dvir.sabinesList.ui.ErrorText
import com.hotmail.or_dvir.sabinesList.ui.LoadingContent
import com.hotmail.or_dvir.sabinesList.ui.NewEditNameDialogState
import com.hotmail.or_dvir.sabinesList.ui.SabinesListAlertDialog
import com.hotmail.or_dvir.sabinesList.ui.SabinesListCustomDialog
import com.hotmail.or_dvir.sabinesList.ui.SearchTopAppBar
import com.hotmail.or_dvir.sabinesList.ui.SharedMenu
import com.hotmail.or_dvir.sabinesList.ui.SwipeToDeleteOrEdit
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsScreenModel.UserEvent.BottomNavigationItemClicked
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsScreenModel.UserEvent.ChangeItemCheckedState
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsScreenModel.UserEvent.CreateNewItem
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsScreenModel.UserEvent.DeleteItem
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsScreenModel.UserEvent.MarkAllItemsUnchecked
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsScreenModel.UserEvent.RenameItem
import com.hotmail.or_dvir.sabinesList.ui.mainActivity.MainActivityViewModel
import com.hotmail.or_dvir.sabinesList.ui.rememberDeleteConfirmationDialogState
import com.hotmail.or_dvir.sabinesList.ui.rememberNewEditNameDialogState
import com.hotmail.or_dvir.sabinesList.ui.theme.LocalBottomNavigationColors
import com.hotmail.or_dvir.sabinesList.ui.theme.fabContentColor
import com.hotmail.or_dvir.sabinesList.ui.theme.menuIconColor
import kotlinx.coroutines.flow.collectLatest

private typealias OnUserEvent = (event: SharedUserEvent) -> Unit

data class ListItemsScreen(val list: UserList) : Screen {

    @Composable
    override fun Content() {
        val screenModel =
            getScreenModel<ListItemsScreenModel, ListItemsScreenModel.Factory> {
                it.create(list.id)
            }
        val mainViewModel = getViewModel<MainActivityViewModel>()
        val context = LocalContext.current

        var showUncheckAllItemsDialog by remember { mutableStateOf(false) }
        val newItemDialogState = rememberNewEditNameDialogState()

        val listItems by screenModel.listItemsFlow.collectAsStateLifecycleAware(emptyList())
        val isLoading by screenModel.isLoadingFlow.collectAsStateLifecycleAware(true)
        val isSearchActive by screenModel.isSearchActiveFlow.collectAsStateLifecycleAware(false)
        val searchQuery by screenModel.searchQueryFlow.collectAsStateLifecycleAware("")
        val selectedBottomNavItem by screenModel.currentBottomNavigationItemFlow.collectAsStateLifecycleAware(BottomNavigationListItem.AllItems)
        val isDarkMode = mainViewModel.collectIsDarkMode()

        LaunchedEffect(Unit) {
            screenModel.sideEffectsFlow.collectLatest { sideEffect ->
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
                is ChangeTheme -> mainViewModel.setDarkMode(event.isDark)
                else -> screenModel.onUserEvent(event)
            }
        }

        Scaffold(
            topBar = {
                ScreenTopAppBar(
                    listItems = listItems,
                    isSearchActive = isSearchActive,
                    searchQuery = searchQuery,
                    isDarkMode = isDarkMode,
                    onUncheckAllClicked = { showUncheckAllItemsDialog = true },
                    onUserEvent = onUserEvent
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    contentColor = MaterialTheme.colors.fabContentColor,
                    onClick = { newItemDialogState.show = true }
                ) {
                    Icon(
                        contentDescription = stringResource(R.string.contentDescription_addListItem),
                        painter = painterResource(R.drawable.ic_add)
                    )
                }
            },
            bottomBar = {
                BottomNavigationBar(
                    selectedItem = selectedBottomNavItem,
                    onUserEvent = onUserEvent
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                ScreenContent(
                    listItems = listItems,
                    isLoading = isLoading,
                    isSearchActive = isSearchActive,
                    searchQuery = searchQuery,
                    onUserEvent = onUserEvent
                )

                newItemDialogState.apply {
                    NewEditItemDialog(
                        state = this,
                        onConfirm = { onUserEvent(CreateNewItem(userInput)) },
                        onDismiss = { reset() }
                    )
                }

                SabinesListAlertDialog(
                    show = showUncheckAllItemsDialog,
                    messageRes = R.string.listItemsScreen_uncheckAllConfirmation,
                    positiveButtonRes = R.string.listItemsScreen_uncheck,
                    onConfirm = {
                        onUserEvent(MarkAllItemsUnchecked)
                        showUncheckAllItemsDialog = false
                    },
                    onDismiss = { showUncheckAllItemsDialog = false }
                )
            }
        }
    }

    @Composable
    private fun BottomNavigationBar(
        selectedItem: BottomNavigationListItem,
        onUserEvent: OnUserEvent
    ) {
        BottomNavigation(Modifier.fillMaxWidth()) {
            BottomNavigationListItem.AllItems.apply {
                ListItemsBottomNavigationItem(
                    item = this,
                    isSelected = selectedItem is BottomNavigationListItem.AllItems,
                    onClick = { onUserEvent(BottomNavigationItemClicked(this)) }
                )
            }

            BottomNavigationListItem.CheckedItems.apply {
                ListItemsBottomNavigationItem(
                    item = this,
                    isSelected = selectedItem is BottomNavigationListItem.CheckedItems,
                    onClick = { onUserEvent(BottomNavigationItemClicked(this)) }
                )
            }

            BottomNavigationListItem.UncheckedItems.apply {
                ListItemsBottomNavigationItem(
                    item = this,
                    isSelected = selectedItem is BottomNavigationListItem.UncheckedItems,
                    onClick = { onUserEvent(BottomNavigationItemClicked(this)) }
                )
            }
        }
    }

    @Composable
    private fun RowScope.ListItemsBottomNavigationItem(
        item: BottomNavigationListItem,
        isSelected: Boolean,
        onClick: () -> Unit
    ) {
        BottomNavigationItem(
            selected = isSelected,
            onClick = onClick,
            selectedContentColor = LocalBottomNavigationColors.current.selected,
            unselectedContentColor = LocalBottomNavigationColors.current.unselected,
            label = { Text(stringResource(item.textRes)) },
            icon = {
                Icon(
                    painter = painterResource(item.iconRes),
                    contentDescription = stringResource(item.contentDescriptionRes)
                )
            }
        )
    }

    @Composable
    private fun ScreenContent(
        listItems: List<ListItem>,
        isLoading: Boolean,
        isSearchActive: Boolean,
        searchQuery: String,
        onUserEvent: OnUserEvent
    ) {
        when {
            isLoading -> LoadingContent()

            listItems.isEmpty() && !isSearchActive -> EmptyContent(
                textRes = R.string.listItemsScreen_emptyView
            )

            listItems.isEmpty() && isSearchActive -> EmptyContent(
                textRes = R.string.search_noResults,
                showAddItemButton = searchQuery.isNotBlank(),
                onAddItemClicked = { onUserEvent(CreateNewItem(searchQuery)) },
            )

            else -> NonEmptyContent(
                listItems = listItems,
                onUserEvent = onUserEvent
            )
        }
    }

    @Composable
    private fun ScreenTopAppBar(
        listItems: List<ListItem>,
        isSearchActive: Boolean,
        searchQuery: String,
        isDarkMode: Boolean,
        onUncheckAllClicked: () -> Unit,
        onUserEvent: OnUserEvent
    ) {
        val context = LocalContext.current

        if (isSearchActive) {
            SearchTopAppBar(
                searchQuery = searchQuery,
                onSearchQueryChanged = { onUserEvent(SearchQueryChanged(it)) },
                onExitSearch = { onUserEvent(SearchActiveStateChanged(false)) }
            )
        } else {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(
                        text = list.name ,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    val navigator = LocalNavigator.current
                    IconButton(onClick = { navigator?.pop() }) {
                        Icon(
                            contentDescription = stringResource(R.string.contentDescription_back),
                            painter = painterResource(R.drawable.ic_arrow_back)
                        )
                    }
                },
                actions = {
                    SharedMenu(
                        isDarkTheme = isDarkMode,
                        onChangeTheme = { onUserEvent(ChangeTheme(it)) },
                        onSearchClicked = { onUserEvent(SearchActiveStateChanged(true)) },
                        extraMenuAction = {
                            if (listItems.isNotEmpty()) {
                                IconButton(onUncheckAllClicked) {
                                    Icon(
                                        tint = MaterialTheme.colors.menuIconColor,
                                        painter = painterResource(R.drawable.ic_uncheck_all),
                                        contentDescription = stringResource(R.string.menuItem_uncheckAll)
                                    )
                                }
                            }
                        },
                        extraOverflowActions = { superOnClick ->
                            if (listItems.isNotEmpty()) {
                                DropdownMenuItem(onClick = {
                                    superOnClick()

                                    val shareText =
                                        context.getString(
                                            R.string.shareListItemsPreText_s_s,
                                            list.name,
                                            listItems.joinToString("\n") { it.name }
                                        )

                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                    }

                                    val shareIntent = Intent.createChooser(sendIntent, null)
                                    startActivity(context, shareIntent, null)
                                }) {
                                    Text(stringResource(R.string.menuItem_share))
                                }
                            }
                        }
                    )
                }
            )
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
                    state.userInput = ""
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    //todo make this take focus
                    //  warning: creates a chain reaction of changes...
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
                    onUserEvent = onUserEvent,
                    onRequestDelete = { id ->
                        deleteItemState.apply {
                            objToDeleteId = id
                            show = true
                        }
                    },
                    onRequestRename = { id, name ->
                        editItemState.apply {
                            userInput = name
                            editedId = id
                            show = true
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
                onConfirm = { onUserEvent(DeleteItem(objToDeleteId)) },
                onDismiss = { reset() }
            )
        }

        editItemState.apply {
            NewEditItemDialog(
                state = this,
                onDismiss = { reset() },
                onConfirm = { editedId?.let { onUserEvent(RenameItem(it, userInput)) } }
            )
        }
    }

    @Composable
    private fun LazyItemScope.ListItemRow(
        listItem: ListItem,
        onUserEvent: OnUserEvent,
        onRequestDelete: (id: Int) -> Unit,
        onRequestRename: (id: Int, name: String) -> Unit
    ) {
        val updatedItem by rememberUpdatedState(listItem)

        SwipeToDeleteOrEdit(
            onDeleteRequest = { onRequestDelete(updatedItem.id) },
            onEditRequest = { onRequestRename(updatedItem.id, updatedItem.name) }
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
                        onUserEvent(ChangeItemCheckedState(updatedItem.id, it))
                    }
                )
            }
        }
    }
}
