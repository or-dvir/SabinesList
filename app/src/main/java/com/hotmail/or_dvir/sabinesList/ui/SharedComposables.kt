package com.hotmail.or_dvir.sabinesList.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.navigator.LocalNavigator
import com.hotmail.or_dvir.sabinesList.R
import com.hotmail.or_dvir.sabinesList.collectAsStateLifecycleAware
import com.hotmail.or_dvir.sabinesList.preferences.ThemePreference
import com.hotmail.or_dvir.sabinesList.ui.theme.menuIconColor

private const val TOP_APP_BAR_MENU_ITEM_LIMIT = 2

@Composable
fun SabinesListAlertDialog(
    show: Boolean,
    @StringRes messageRes: Int,
    @StringRes positiveButtonRes: Int,
    @StringRes negativeButtonRes: Int = R.string.cancel,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) {
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text(stringResource(positiveButtonRes))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(negativeButtonRes))
            }
        },
        text = { Text(stringResource(messageRes)) }
    )
}

@Composable
internal fun NavigationIconBackArrow() {
    val navigator = LocalNavigator.current
    IconButton(onClick = { navigator?.pop() }) {
        Icon(
            contentDescription = stringResource(R.string.contentDescription_back),
            painter = painterResource(R.drawable.ic_arrow_back)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.SwipeToDeleteOrEdit(
    onDeleteRequest: () -> Unit,
    onEditRequest: () -> Unit,
    dismissContent: @Composable RowScope.() -> Unit
) {
    val deleteDirection by remember { mutableStateOf(DismissDirection.StartToEnd) }
    val deleteDismissValue by remember { mutableStateOf(DismissValue.DismissedToEnd) }

    val editDirection by remember { mutableStateOf(DismissDirection.EndToStart) }
    val editDismissValue by remember { mutableStateOf(DismissValue.DismissedToStart) }

    val dismissState = rememberDismissState(
        confirmStateChange = {
            //do NOT change this with if-else statement! there are other options
            //for DismissValue which we need to ignore!
            when (it) {
                deleteDismissValue -> onDeleteRequest()
                editDismissValue -> onEditRequest()
                else -> { /* do nothing. */
                }
            }

            //it's up to the caller to actually "dismiss" the item
            // e.g. remove it from the data source
            false
        }
    )

    SwipeToDismiss(
        modifier = Modifier
            .fillMaxWidth()
            .padding()
            .animateItemPlacement(),
        dismissThresholds = { FractionalThreshold(0.5f) },
        state = dismissState,
        directions = setOf(deleteDirection, editDirection),
        dismissContent = dismissContent,
        background = {
            //should help with performance
            dismissState.dismissDirection?.let {
                when (it) {
                    deleteDirection -> SwipeBackground(
                        color = Color.Red,
                        iconRes = R.drawable.ic_delete,
                        imageArrangement = Arrangement.Start
                    )

                    editDirection -> SwipeBackground(
                        color = MaterialTheme.colors.secondary,
                        iconRes = R.drawable.ic_edit,
                        imageArrangement = Arrangement.End
                    )

                    else -> { /*do nothing*/
                    }
                }
            }
        }
    )
}

@Composable
fun EmptyContent(
    @StringRes textRes: Int,
    showAddItemButton: Boolean = false,
    onAddItemClicked: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (showAddItemButton) Arrangement.Top else Arrangement.Center
    ) {
        Text(stringResource(textRes))

        if (showAddItemButton) {
            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                shape = CircleShape,
                onClick = { onAddItemClicked?.invoke() }
            ) {
                Text(stringResource(R.string.listItemsScreen_addListItem))
            }
        }
    }
}

@Composable
fun SearchTopAppBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onExitSearch: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        value = searchQuery,
        placeholder = { Text(stringResource(R.string.search)) },
        singleLine = true,
        onValueChange = onSearchQueryChanged,
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null
            )
        },
        trailingIcon = {
            IconButton(onClick = {
                if (searchQuery.isBlank()) {
                    onExitSearch()
                } else {
                    onSearchQueryChanged("")
                }
            }) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = stringResource(
                        if (searchQuery.isBlank()) {
                            R.string.contentDescription_exitSearch
                        } else {
                            R.string.contentDescription_clearSearchQuery
                        }
                    )
                )
            }
        },
    )
}

@Composable
fun TopAppBarActions(
    menuItems: List<MenuItemInfo>,
    onItemClicked: (MenuItemInfo) -> Unit
) {
    val firstTwo = menuItems.take(TOP_APP_BAR_MENU_ITEM_LIMIT)
    val everythingElse = menuItems.drop(TOP_APP_BAR_MENU_ITEM_LIMIT)

    firstTwo.forEach {
        IconButton(onClick = { onItemClicked(it) }) {
            Icon(
                tint = MaterialTheme.colors.menuIconColor,
                contentDescription = stringResource(it.label),
                painter = painterResource(it.iconRes)
            )
        }
    }

    var showOverflowMenu by remember { mutableStateOf(false) }

    IconButton(onClick = { showOverflowMenu = !showOverflowMenu }) {
        Icon(
            tint = MaterialTheme.colors.menuIconColor,
            contentDescription = stringResource(R.string.contentDescription_moreActions),
            painter = painterResource(R.drawable.ic_more_vert)
        )
    }

    val collapseOverflowMenu = { showOverflowMenu = false }

    DropdownMenu(
        expanded = showOverflowMenu,
        onDismissRequest = collapseOverflowMenu
    ) {
        everythingElse.forEach {
            DropdownMenuItem(onClick = {
                collapseOverflowMenu()
                onItemClicked(it)
            }) {
                Text(stringResource(it.label))
            }
        }
    }
}

@Composable
private fun SwipeBackground(
    color: Color,
    @DrawableRes iconRes: Int,
    imageArrangement: Arrangement.Horizontal
) {
    Row(
        horizontalArrangement = imageArrangement,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = 16.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null
        )
    }
}

@Composable
fun ErrorText(
    @StringRes errorRes: Int
) {
    Text(
        text = stringResource(errorRes),
        color = MaterialTheme.colors.error,
        style = MaterialTheme.typography.caption,
        modifier = Modifier.padding(start = 16.dp, top = 5.dp)
    )
}

@Composable
fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun SabinesListCustomDialog(
    @StringRes titleRes: Int,
    @StringRes positiveButtonRes: Int,
    positiveButtonEnabled: Boolean,
    onPositiveButtonClick: () -> Unit,
    onDismiss: () -> Unit,
    @StringRes negativeButtonRes: Int = R.string.cancel,
    @StringRes neutralButtonRes: Int? = null,
    neutralButtonEnabled: Boolean = true,
    onNeutralButtonClicked: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(5.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                //title
                Text(
                    text = stringResource(titleRes),
                    style = MaterialTheme.typography.h6
                )

                Spacer(modifier = Modifier.height(16.dp))

                //body
                content()

                Spacer(modifier = Modifier.height(5.dp))

                //buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    //neutral button
                    Row {
                        neutralButtonRes?.let {
                            TextButton(
                                enabled = neutralButtonEnabled,
                                onClick = { onNeutralButtonClicked?.invoke() }
                            ) {
                                Text(stringResource(neutralButtonRes))
                            }
                        }
                    }

                    //positive and negative buttons
                    Row {
                        //negative button
                        TextButton(onClick = onDismiss) {
                            Text(stringResource(negativeButtonRes))
                        }

                        //positive button
                        TextButton(
                            enabled = positiveButtonEnabled,
                            onClick = onPositiveButtonClick
                        ) {
                            Text(stringResource(positiveButtonRes))
                        }
                    }
                }


//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.End
//                ) {
//                    //negative button
//                    TextButton(onClick = onDismiss) {
//                        Text(stringResource(negativeButtonRes))
//                    }
//
//                    //positive button
//                    TextButton(
//                        enabled = positiveButtonEnabled,
//                        onClick = onPositiveButtonClick
//                    ) {
//                        Text(stringResource(positiveButtonRes))
//                    }
//                }
            }
        }
    }
}
