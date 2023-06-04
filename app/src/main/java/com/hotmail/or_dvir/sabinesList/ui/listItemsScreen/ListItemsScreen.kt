package com.hotmail.or_dvir.sabinesList.ui.listItemsScreen

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
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
import com.hotmail.or_dvir.sabinesList.toUserFriendlyText
import com.hotmail.or_dvir.sabinesList.ui.DeleteConfirmationDialog
import com.hotmail.or_dvir.sabinesList.ui.ErrorText
import com.hotmail.or_dvir.sabinesList.ui.SharedOverflowMenu
import com.hotmail.or_dvir.sabinesList.ui.SwipeToDeleteOrEdit
import com.hotmail.or_dvir.sabinesList.ui.SabinesListDialog
import com.hotmail.or_dvir.sabinesList.ui.collectIsDarkMode
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsViewModel.UserEvent
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsViewModel.UserEvent.OnDeleteListItem
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsViewModel.UserEvent.OnNewOrEditListItem
import com.hotmail.or_dvir.sabinesList.ui.mainActivity.MainActivityViewModel
import com.hotmail.or_dvir.sabinesList.ui.rememberDeleteConfirmationDialogState
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime

private typealias OnUserEvent = (event: UserEvent) -> Unit

data class ListItemsScreen(val list: UserList) : Screen {
    // todo
    //  change process name (fully qualified app name)
    //      first test with RELEASE flavor

    @Composable
    override fun Content() {
        val mainViewModel = getViewModel<MainActivityViewModel>()
        val screenViewModel =
            getScreenModel<ListItemsViewModel, ListItemsViewModel.Factory> {
                it.create(list.id)
            }

        val newItemDialogState = rememberNewEditListItemState()
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
                            screenViewModel.onUserEvent(
                                OnNewOrEditListItem(
                                    ListItem(
                                        startDate = startDate,
                                        endDate = endDate,
                                        startTime = startTime,
                                        endTime = endTime,
                                        note = note,
                                        listId = list.id
                                    )
                                )
                            )

                            Toast.makeText(
                                context,
                                R.string.itemAdded,
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
    private fun rememberNewEditListItemState() = remember { NewEditListItemDialogState() }

    @Composable
    private fun NewEditItemDialog(
        state: NewEditListItemDialogState,
        onConfirm: () -> Unit,
        onDismiss: () -> Unit
    ) {
        if (!state.show) {
            return
        }

        val isEditing by remember(state.editedItemId) {
            mutableStateOf(state.editedItemId != null)
        }

        state.apply {
            SabinesListDialog(
                titleRes = if (isEditing) R.string.dialogTitle_newItem else R.string.dialogTitle_newItem,
                positiveButtonRes = if (isEditing) R.string.edit else R.string.create,
                onDismiss = onDismiss,
                positiveButtonEnabled = !errorEndTimeBeforeStartTime,
                onPositiveButtonClick = {
                    onConfirm()
                    onDismiss()
                }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val selectableStartTimeRange by remember {
                        derivedStateOf {
                            val maxStartTime = if (!areStartEndSameDay) {
                                LocalTime.MAX
                            } else {
                                endTime ?: LocalTime.MAX
                            }

                            LocalTime.MIN..maxStartTime
                        }
                    }

                    val selectableEndTimeRange by remember {
                        derivedStateOf {
                            val minEndTime = if (!areStartEndSameDay) {
                                LocalTime.MIN
                            } else {
                                startTime ?: LocalTime.MIN
                            }

                            minEndTime..LocalTime.MAX
                        }
                    }

                    // todo align the dates text to the start of the longest word "start:" or "end:"
                    //  because of the error text, looks like the only way to do this is to use
                    //  constraint layout... do it later
                    // start date/time
                    StartEndDateTimeRow(
                        preText = R.string.preText_start,
                        removableStartDate = false,
                        selectedDate = startDate,
                        minSelectableDate = LocalDate.MIN,
                        maxSelectableDate = endDate ?: LocalDate.MAX,
                        selectedTime = startTime,
                        selectableTimeRange = selectableStartTimeRange,
                        onTimeChanged = { startTime = it },
                        onDateChanged = {
                            //since we set removableStartDate to `false`, `it` should not be null here
                            startDate = it!!
                        }
                    )

                    // end date/time. wrapper in extra Column so that the error appears right beneath it
                    //(ignores the "spacedBy" of the containing column
                    Column {
                        StartEndDateTimeRow(
                            preText = R.string.preText_end,
                            selectedDate = endDate,
                            minSelectableDate = startDate,
                            maxSelectableDate = LocalDate.MAX,
                            selectedTime = endTime,
                            selectableTimeRange = selectableEndTimeRange,
                            onDateChanged = { endDate = it },
                            onTimeChanged = { endTime = it }
                        )

                        if (errorEndTimeBeforeStartTime) {
                            ErrorText(R.string.error_endTimeBeforeStartTime)
                        }
                    }

                    // note
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 290.dp),
                        value = note,
                        onValueChange = { note = it },
                        label = { Text(stringResource(R.string.hint_note)) }
                    )
                }
            }
        }
    }

    @Composable
    private fun StartEndDateTimeRow(
        @StringRes preText: Int,
        selectedDate: LocalDate?,
        minSelectableDate: LocalDate,
        maxSelectableDate: LocalDate,
        selectedTime: LocalTime?,
        selectableTimeRange: ClosedRange<LocalTime>,
        onDateChanged: (LocalDate?) -> Unit,
        onTimeChanged: (LocalTime?) -> Unit,
        removableStartDate: Boolean = true
    ) {
        val datePickerState = rememberMaterialDialogState()
        val timePickerState = rememberMaterialDialogState()

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(preText))
            Spacer(Modifier.width(5.dp))

            //date
            Text(
                text = selectedDate?.toUserFriendlyText() ?: stringResource(R.string.setDate),
                color = MaterialTheme.colors.secondary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { datePickerState.show() }
            )
            Spacer(Modifier.width(8.dp))

            //time
            Text(
                text = selectedTime?.toUserFriendlyText() ?: stringResource(R.string.setTime),
                color = MaterialTheme.colors.secondary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { timePickerState.show() }
            )
        }

        //date picker
        MaterialDialog(
            dialogState = datePickerState,
            buttons = {
                positiveButton(res = R.string.set)
                negativeButton(res = R.string.cancel)
                if (removableStartDate) {
                    //todo
                    // change text to "delete"?
                    // button is in the middle, how can i move to end?
                    button(
                        res = R.string.remove,
                        onClick = {
                            onDateChanged(null)
                            datePickerState.hide()
                        }
                    )
                }
            },
        ) {
            val selectableDates by remember {
                derivedStateOf { minSelectableDate..maxSelectableDate }
            }

            datepicker(
                title = "",
                onDateChange = onDateChanged,
                initialDate = selectedDate ?: LocalDate.now(),
                allowedDateValidator = { selectableDates.contains(it) }
            )
        }

        //time picker
        MaterialDialog(
            dialogState = timePickerState,
            buttons = {
                positiveButton(res = R.string.set)
                negativeButton(res = R.string.cancel)
                //todo
                // change text to "delete"?
                // button is in the middle, how can i move to end?
                button(
                    res = R.string.remove,
                    onClick = {
                        onTimeChanged(null)
                        timePickerState.hide()
                    }
                )
            },
        ) {
            timepicker(
                timeRange = selectableTimeRange,
                is24HourClock = true,
                title = "",
                onTimeChange = onTimeChanged,
                initialTime = selectedTime ?: LocalTime.now()
            )
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
        val editItemState = rememberNewEditListItemState()

        LazyColumn {
            itemsIndexed(
                items = listItems,
                key = { _, listItem -> listItem.id },
            ) { index, listItem ->
                ListItemRow(
                    listItem = listItem,
                    onUserEvent = { userEvent ->
                        when (userEvent) {
                            is OnDeleteListItem -> deleteItemState.apply {
                                objToDeleteId = userEvent.itemId
                                show = true
                            }
                            is OnNewOrEditListItem -> editItemState.apply {
                                setFromListItem(userEvent.item)
                                show = true
                            }
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
            DeleteConfirmationDialog(
                state = this,
                messageRes = R.string.listItemsScreen_deleteConfirmation,
                onConfirm = { onUserEvent(OnDeleteListItem(objToDeleteId)) },
                onDismiss = { reset() }
            )
        }

        editItemState.apply {
            NewEditItemDialog(
                state = this,
                onDismiss = { reset() },
                onConfirm = {
                    editedItemId?.let {
                        onUserEvent(
                            OnNewOrEditListItem(
                                ListItem(
                                    startDate = startDate,
                                    endDate = endDate,
                                    startTime = startTime,
                                    endTime = endTime,
                                    note = note,
                                    listId = list.id,
                                    id = it
                                )
                            )
                        )
                    }
                }
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
            onDeleteRequest = { onUserEvent(OnDeleteListItem(updatedItem.id)) },
            onEditRequest = { onUserEvent(OnNewOrEditListItem(updatedItem)) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        if (listItem.hasEndDateTime) {
                            ListItemColumnFromUntil()
                        }

                        ListItemColumnStartEndDateTime(listItem)
                    }

                    listItem.note.takeIf { it.isNotBlank() }?.let {
                        Spacer(Modifier.height(5.dp))
                        Text(
                            modifier = Modifier
                                .heightIn(max = 175.dp)
                                .padding(start = 8.dp)
                                .verticalScroll(rememberScrollState()),
                            text = it,
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ListItemColumnStartEndDateTime(listItem: ListItem) {
        listItem.apply {
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(startDate.toUserFriendlyText())
                    startTime?.let { Text(it.toUserFriendlyText()) }
                }
                if (hasEndDateTime) {
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        endDate?.let { Text(it.toUserFriendlyText()) }
                        endTime?.let { Text(it.toUserFriendlyText()) }
                    }
                }
            }
        }
    }

    @Composable
    private fun ListItemColumnFromUntil() {
        Column(
            modifier = Modifier.width(IntrinsicSize.Max)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.from))
                Text(":")
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.until))
                Text(":")
            }
        }
    }
}
