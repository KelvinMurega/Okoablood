 package com.example.okoablood.ui.screens.home

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
    onNavigateToNotifications: () -> Unit,
    onOpenDrawer: () -> Unit = {},
    bloodRequestViewModel: BloodRequestViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            OkoaBloodTopAppBar(
                title = "OkoaBlood",
                onBack = null,
                showBackButton = false,
                actions = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
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

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        // Quick Action Buttons Section
                        item {
                            QuickActionButtons(
                                onFindDonors = onNavigateToDonors,
                                onHospitals = {
                                    // TODO: Navigate to hospitals screen when implemented
                                    Toast.makeText(context, "Hospitals feature coming soon!", Toast.LENGTH_SHORT).show()
                                },
                                onUrgentRequests = onNavigateToNotifications,
                                onRequestBlood = onNavigateToRequestBlood
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        item {
                            Text(
                                text = "Urgent Requests",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
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

                        if (recentRequests.isEmpty() && urgentRequests.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    EmptyStateMessage(
                                        message = "No blood requests found.\nCreate a new request by tapping the + button."
                                    )
                                }
                            }
                        } else {
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
}

@Composable
fun QuickActionButtons(
    onFindDonors: () -> Unit,
    onHospitals: () -> Unit,
    onUrgentRequests: () -> Unit,
    onRequestBlood: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Request Blood Button - Prominent
        Button(
            onClick = onRequestBlood,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Request Blood",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Other Quick Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionButton(
                title = "Find Donors",
                icon = Icons.Default.Group,
                onClick = onFindDonors,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                title = "Hospitals",
                icon = Icons.Default.LocalHospital,
                onClick = onHospitals,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                title = "Urgent",
                icon = Icons.Default.Warning,
                onClick = onUrgentRequests,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickActionButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}


