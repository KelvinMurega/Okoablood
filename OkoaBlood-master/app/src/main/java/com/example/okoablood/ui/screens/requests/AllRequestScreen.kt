package com.example.okoablood.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import com.example.okoablood.data.model.BloodRequest
import com.example.okoablood.viewmodel.BloodRequestViewModel

// --- IMPORTS FOR YOUR STANDARD COMPONENTS ---
import com.example.okoablood.ui.components.BloodRequestCard
import com.example.okoablood.ui.components.EmptyStateMessage
import com.example.okoablood.ui.components.ErrorMessage
import com.example.okoablood.ui.components.LoadingIndicator
import com.example.okoablood.ui.components.OkoaBloodTopAppBar
// --- IMPORT THE CENTRALIZED SearchField ---
import com.example.okoablood.ui.components.SearchField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllRequestsScreen(
    viewModel: BloodRequestViewModel,
    onBack: () -> Unit,
    onRequestSelected: (String) -> Unit,
    onDonorSelected: (String) -> Unit,
    onCreateNewRequest: () -> Unit,
) {
    val bloodRequestsState by viewModel.bloodRequestsState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadAllBloodRequests()
    }

    Scaffold(
        topBar = {
            OkoaBloodTopAppBar(
                title = "All Blood Requests",
                showBackButton = true,
                onBack = onBack
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // --- USE THE CENTRALIZED SearchField ---
            SearchField(
                query = searchQuery,
                onQueryChanged = { searchQuery = it },
                placeholderText = "Filter by location (e.g., Nairobi)"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                when (val state = bloodRequestsState) {
                    is BloodRequestViewModel.BloodRequestsState.Loading -> LoadingState()
                    is BloodRequestViewModel.BloodRequestsState.Success -> {

                        val filteredList = if (searchQuery.isEmpty()) {
                            state.requests
                        } else {
                            state.requests.filter {
                                it.location.contains(searchQuery, ignoreCase = true)
                            }
                        }

                        BloodRequestsList(
                            bloodRequests = filteredList,
                            onRequestSelected = onRequestSelected
                        )
                    }
                    is BloodRequestViewModel.BloodRequestsState.Error -> ErrorState(state.message)
                }
            }
        }
    }
}

// --- SearchField function is REMOVED from this file ---

@Composable
fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LoadingIndicator()
    }
}

@Composable
fun BloodRequestsList(
    bloodRequests: List<BloodRequest>,
    onRequestSelected: (String) -> Unit
) {
    if (bloodRequests.isEmpty()) {
        EmptyStateMessage(message = "No blood requests found.")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(bloodRequests) { request ->
            BloodRequestCard(
                bloodRequest = request,
                onClick = { onRequestSelected(request.id) }
            )
        }
    }
}

@Composable
fun ErrorState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ErrorMessage(message = message)
    }
}