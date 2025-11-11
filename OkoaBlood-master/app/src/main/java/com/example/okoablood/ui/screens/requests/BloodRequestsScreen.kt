package com.example.okoablood.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.okoablood.data.model.BloodRequest
import com.example.okoablood.ui.theme.OkoaBloodTheme
import com.example.okoablood.viewmodel.BloodRequestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodRequestsScreen(
    viewModel: BloodRequestViewModel,
    onSubmitRequest: () -> Unit,
    onBack: () -> Unit

    ) {
    // Fetch filtered blood requests
    val bloodRequestsState = viewModel.filteredBloodRequestsState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(searchQuery) {
        viewModel.filterBloodRequests(searchQuery)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Blood Requests") }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            SearchField(
                query = searchQuery,
                onQueryChanged = { searchQuery = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (val state = bloodRequestsState.value) {
                is BloodRequestViewModel.BloodRequestsState.Loading -> {
                    LoadingState()
                }
                is BloodRequestViewModel.BloodRequestsState.Success -> {
                    BloodRequestsList(bloodRequests = state.requests)
                }
                is BloodRequestViewModel.BloodRequestsState.Error -> {
                    ErrorState(message = state.message)
                }
            }
        }
    }
}

@Composable
fun SearchField(query: String, onQueryChanged: (String) -> Unit) {
    BasicTextField(
        value = query,
        onValueChange = onQueryChanged,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(onSearch = { /* handle search */ }),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(56.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp)
    )
}
