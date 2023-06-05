package com.hotmail.or_dvir.sabinesList.ui.listItemsScreen

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.hotmail.or_dvir.sabinesList.isBefore
import com.hotmail.or_dvir.sabinesList.models.ListItem
import java.time.LocalDate
import java.time.LocalTime

//todo class copied from NewEditListDialogState. make a single class
class NewEditListItemDialogState {
    var show by mutableStateOf(false)
    var userInput by mutableStateOf("")
    val isError by derivedStateOf { userInput.isBlank() }

    var editedItemId: Int? by mutableStateOf(null)

    fun reset() {
        show = false
        userInput = ""
        editedItemId = null
    }
}
