package com.example.okoablood.ui.screens.home

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.okoablood.ui.components.*
import com.example.okoablood.viewmodel.BloodRequestViewModel
import com.example.okoablood.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToProfile: () -> Unit,
    onNavigateToDonors: () -> Unit,
    onNavigateToRequests: () -> Unit,
    onNavigateToRequestDetails: (String) -> Unit,
    onNavigateToRequestBlood: () -> Unit,
    bloodRequestViewModel: BloodRequestViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    Scaffold(

        topBar = {
            OkoaBloodTopAppBar(title = "Okoa Blood",
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                    IconButton(onClick = onNavigateToDonors) {
                        Icon(Icons.Default.Group, contentDescription = "Donors")
                    }
                    IconButton(onClick = {
                        Toast.makeText(context, "Notifications coming soon!", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }

                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Okoa Blood App")
                            putExtra(Intent.EXTRA_TEXT, "Join me in donating blood with Okoa Blood!")
                        }
                        context.startActivity(Intent.createChooser(intent, "Share via"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share App")
                    }


                })

        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToRequestBlood,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Request Blood",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        LoadingIndicator()
                    }
                }

                uiState.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ErrorMessage(message = uiState.error ?: "An error occurred")
                    }
                }

                uiState.bloodRequests.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        EmptyStateMessage(
                            message = "No blood requests found.\nCreate a new request by tapping the + button."
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        item {
                            Text(
                                text = "Urgent Requests",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        val urgentRequests = uiState.bloodRequests.filter { it.urgent }

                        if (urgentRequests.isEmpty()) {
                            item {
                                Text(
                                    text = "No urgent requests at the moment",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        } else {
                            items(urgentRequests) { request ->
                                BloodRequestCard(
                                    bloodRequest = request,
                                    onClick = { onNavigateToRequestDetails(request.id) }
                                )
                            }
                        }

                        item {
                            SectionHeader(title = "All Blood Requests")
                        }

                        val recentRequests = uiState.bloodRequests.filter { !it.urgent }

                        items(recentRequests) { request ->
                            BloodRequestCard(
                                bloodRequest = request,
                                onClick = { onNavigateToRequestDetails(request.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}


