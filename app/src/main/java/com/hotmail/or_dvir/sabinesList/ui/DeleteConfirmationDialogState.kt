package com.hotmail.or_dvir.sabinesList.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class DeleteConfirmationDialogState {
    var show by mutableStateOf(false)
    var objToDeleteId = DUMMY_ID

    fun reset() {
        show = false
        objToDeleteId = DUMMY_ID
    }

    private companion object {
        const val DUMMY_ID = -1
    }
}

@Composable
fun rememberDeleteConfirmationDialogState() = remember { DeleteConfirmationDialogState() }
