package com.example.okoablood.ui.screens.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.okoablood.ui.components.BloodRequestCard
import com.example.okoablood.ui.components.EmptyStateMessage
import com.example.okoablood.ui.components.OkoaBloodTopAppBar
import com.example.okoablood.viewmodel.BloodRequestViewModel
import com.example.okoablood.di.DependencyProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    onRequestClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = remember { DependencyProvider.provideBloodRequestViewModel() }
    val state by viewModel.bloodRequestsState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAllBloodRequests()
    }

    Scaffold(
        topBar = {
            OkoaBloodTopAppBar(
                title = "Urgent Requests",
                onBack = null,
                showBackButton = false
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val s = state) {
                is BloodRequestViewModel.BloodRequestsState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is BloodRequestViewModel.BloodRequestsState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = s.message, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                is BloodRequestViewModel.BloodRequestsState.Success -> {
                    val urgentRequests = remember(s.requests) { s.requests.filter { it.urgent } }

                    if (urgentRequests.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "No urgent requests",
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                EmptyStateMessage(
                                    message = "No urgent donation requests at the moment.\nCheck back later!"
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                Text(
                                    text = "Urgent blood donation requests",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "These requests need immediate attention",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }

                            items(urgentRequests) { request ->
                                BloodRequestCard(
                                    bloodRequest = request,
                                    onClick = { onRequestClick(request.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

