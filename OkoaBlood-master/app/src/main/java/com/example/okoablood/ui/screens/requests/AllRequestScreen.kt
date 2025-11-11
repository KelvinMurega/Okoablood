package com.example.okoablood.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.okoablood.data.model.BloodRequest
import com.example.okoablood.viewmodel.BloodRequestViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.okoablood.data.repository.BloodDonationRepository


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllRequestsScreen(
    viewModel: BloodRequestViewModel = viewModel(),
    onBack: () -> Unit,
    onRequestSelected: @Composable (String) -> Unit,
    onDonorSelected: (String) -> Unit,
    onCreateNewRequest: () -> Unit,

    ) {
    val bloodRequestsState by viewModel.bloodRequestsState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAllBloodRequests()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Blood Requests") }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (val state = bloodRequestsState) {
                is BloodRequestViewModel.BloodRequestsState.Loading -> LoadingState()
                is BloodRequestViewModel.BloodRequestsState.Success -> BloodRequestsList(state.requests)
                is BloodRequestViewModel.BloodRequestsState.Error -> ErrorState(state.message)
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun BloodRequestsList(bloodRequests: List<BloodRequest>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(bloodRequests) { request ->
            BloodRequestItem(bloodRequest = request)
        }
    }
}

@Composable
fun BloodRequestItem(bloodRequest: BloodRequest) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Blood Type: ${bloodRequest.bloodGroup}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Urgency: ${bloodRequest.urgent}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Location: ${bloodRequest.location}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ErrorState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Error: $message", color = MaterialTheme.colorScheme.error)
    }
}

