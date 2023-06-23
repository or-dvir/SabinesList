package com.hotmail.or_dvir.sabinesList.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class NewEditNameDialogState {
    var show by mutableStateOf(false)
    var userInput by mutableStateOf("")
    val isError by derivedStateOf { userInput.isBlank() }

    var editedId: Int? by mutableStateOf(null)

    fun reset() {
        show = false
        userInput = ""
        editedId = null
    }
}

@Composable
fun rememberNewEditNameDialogState() = remember { NewEditNameDialogState() }
