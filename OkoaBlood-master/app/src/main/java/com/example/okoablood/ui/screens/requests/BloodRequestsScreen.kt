package com.example.okoablood.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.okoablood.viewmodel.BloodRequestViewModel

// --- IMPORTS FOR YOUR STANDARD COMPONENTS ---
import com.example.okoablood.ui.components.OkoaBloodTopAppBar
// --- IMPORT THE CENTRALIZED SearchField ---
import com.example.okoablood.ui.components.SearchField
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodRequestsScreen(
    viewModel: BloodRequestViewModel,
    onSubmitRequest: () -> Unit,
    onBack: () -> Unit,
    onRequestSelected: (String) -> Unit
) {
    // Fetch filtered blood requests
    val bloodRequestsState = viewModel.filteredBloodRequestsState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(searchQuery) {
        viewModel.filterBloodRequests(searchQuery)
    }

    Scaffold(
        topBar = {
            OkoaBloodTopAppBar(
                title = "Search Requests",
                showBackButton = true,
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            // --- USE THE CENTRALIZED SearchField ---
            SearchField(
                query = searchQuery,
                onQueryChanged = { searchQuery = it },
                placeholderText = "Search by blood group (e.g., A+)"
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (val state = bloodRequestsState.value) {
                is BloodRequestViewModel.BloodRequestsState.Loading -> {
                    // This function now comes from AllRequestScreen.kt
                    LoadingState()
                }
                is BloodRequestViewModel.BloodRequestsState.Success -> {
                    // This function now comes from AllRequestScreen.kt
                    BloodRequestsList(
                        bloodRequests = state.requests,
                        onRequestSelected = onRequestSelected
                    )
                }
                is BloodRequestViewModel.BloodRequestsState.Error -> {
                    // This function now comes from AllRequestScreen.kt
                    ErrorState(message = state.message)
                }
            }
        }
    }
}

// --- SearchField function is REMOVED from this file ---