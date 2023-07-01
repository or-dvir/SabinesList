package com.hotmail.or_dvir.sabinesList.ui.searchScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.hotmail.or_dvir.sabinesList.R

class SearchScreen : Screen {

    //todo this screen should be "no history"

    @Composable
    override fun Content() {
        val viewModel = getViewModel<SearchScreenViewModel>()
        var searchQuery by remember { mutableStateOf("") }

        // todo
        //  add loading icon while searching
        //  when navigating away, use "replace" so that this screen is no longer available

        val resetSearch = { viewModel.search("") }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = searchQuery,
                label = { Text(stringResource(R.string.search)) },
                maxLines = 2,
                onValueChange = { value ->
                    searchQuery = value
                    //this will reset the search results
                    if (value.isBlank()) {
                        resetSearch()
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = ""
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { resetSearch() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.contentDescription_resetSearch)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { viewModel.search(searchQuery) }
                )
            )

            //todo display search results
            //  first half of page - lists
            //  second half od page - items
        }
    }
}
