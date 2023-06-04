package com.hotmail.or_dvir.sabinesList.ui.homeScreen

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class NewEditListDialogState {
    var show by mutableStateOf(false)
    var userInput by mutableStateOf("")
    val isError by derivedStateOf { userInput.isBlank() }

    var editedListId: Int? by mutableStateOf(null)

    fun reset() {
        show = false
        userInput = ""
        editedListId = null
    }
}
