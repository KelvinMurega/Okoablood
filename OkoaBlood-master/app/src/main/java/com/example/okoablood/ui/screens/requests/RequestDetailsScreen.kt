package com.example.okoablood.ui.screens.requests

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.okoablood.ui.viewmodels.RequestDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailsScreen(
    id: String,
    onBack: () -> Unit,
    viewModel: RequestDetailsViewModel
) {
    val request by viewModel.request.collectAsState()
    LaunchedEffect(id) {
        viewModel.loadRequest(id)
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        request?.let {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text("Patient Name: ${it.patientName}", style = MaterialTheme.typography.titleMedium)
                Text("Blood Group: ${it.bloodGroup}", style = MaterialTheme.typography.bodyMedium)
                Text("Location: ${it.location}", style = MaterialTheme.typography.bodyMedium)
                Text("Hospital: ${it.hospital}", style = MaterialTheme.typography.bodyMedium)
                Text("Contact: ${it.requesterPhoneNumber}", style = MaterialTheme.typography.bodyMedium)
                Text("Urgent: ${if (it.urgent) "Yes" else "No"}", style = MaterialTheme.typography.bodyMedium)
                Text("Date: ${it.requestDate}", style = MaterialTheme.typography.bodySmall)
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

