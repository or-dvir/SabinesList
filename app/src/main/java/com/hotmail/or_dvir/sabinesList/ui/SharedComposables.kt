package com.hotmail.or_dvir.sabinesList.ui

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hotmail.or_dvir.sabinesList.R
import com.hotmail.or_dvir.sabinesList.collectAsStateLifecycleAware
import com.hotmail.or_dvir.sabinesList.ui.mainActivity.MainActivityViewModel
import com.hotmail.or_dvir.sabinesList.ui.theme.menuIconColor

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

            //its up to the caller to actually "dismiss" the item
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
                        imageVector = Icons.Filled.Delete,
                        imageArrangement = Arrangement.Start
                    )

                    editDirection -> SwipeBackground(
                        color = MaterialTheme.colors.secondaryVariant,
                        imageVector = Icons.Filled.Edit,
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
fun MainActivityViewModel.collectIsDarkMode() =
    isDarkModeFlow.collectAsStateLifecycleAware(initial = false).value

@Composable
fun EmptyContent(
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
                imageVector = Icons.Default.Search,
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
                    imageVector = Icons.Default.Close,
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
fun SharedOverflowMenu(
    isDarkTheme: Boolean,
    onChangeTheme: (darkTheme: Boolean) -> Unit,
    onSearchClicked: () -> Unit,
    extraAction: @Composable () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showCreditsDialog by remember { mutableStateOf(false) }

    IconButton(onClick = onSearchClicked) {
        Icon(
            tint = MaterialTheme.colors.menuIconColor,
            contentDescription = stringResource(R.string.contentDescription_search),
            imageVector = Icons.Default.Search
        )
    }

    extraAction()

    IconButton(onClick = { showMenu = !showMenu }) {
        Icon(
            tint = MaterialTheme.colors.menuIconColor,
            contentDescription = stringResource(R.string.contentDescription_menu),
            imageVector = Icons.Default.MoreVert
        )
    }

    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false }
    ) {
        DropdownMenuItem(onClick = {
            showMenu = false
            onChangeTheme(!isDarkTheme)
        }) {
            Text(
                stringResource(
                    if (isDarkTheme) R.string.menuItem_lightTheme else R.string.menuItem_darkTheme
                )
            )
        }

        DropdownMenuItem(onClick = {
            showMenu = false
            showCreditsDialog = true
        }) {
            Text(stringResource(R.string.credits_title))
        }
    }

    if (showCreditsDialog) {
        val dismissDialog = { showCreditsDialog = false }

        AlertDialog(
            onDismissRequest = dismissDialog,
            confirmButton = {
                TextButton(onClick = dismissDialog) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            title = { Text(stringResource(R.string.credits_title)) },
            text = { Text(stringResource(R.string.credits)) },
        )
    }
}

@Composable
private fun SwipeBackground(
    color: Color,
    imageVector: ImageVector,
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
            imageVector = imageVector,
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
