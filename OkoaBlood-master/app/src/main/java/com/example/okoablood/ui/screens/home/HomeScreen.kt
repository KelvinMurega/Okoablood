package com.example.okoablood.ui.screens.home


import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
    onNavigateToMap: () -> Unit,
    onOpenDrawer: () -> Unit = {},
    bloodRequestViewModel: BloodRequestViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Filter state
    var selectedBloodGroup by remember { mutableStateOf<String?>(null) }
    var bloodGroupExpanded by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(0) }

    val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    // Apply filters to blood requests (by blood group only; urgent/all handled by tabs)
    val filteredRequests = remember(uiState.bloodRequests, selectedBloodGroup) {
        uiState.bloodRequests.filter { request ->
            val matchesBloodGroup = selectedBloodGroup == null || request.bloodGroup.equals(selectedBloodGroup, ignoreCase = true)
            matchesBloodGroup
        }
    }

    val urgentRequests = filteredRequests.filter { it.urgent }
    val nonUrgentRequests = filteredRequests.filter { !it.urgent }

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
                                onHospitals = onNavigateToMap,
                                onRequestBlood = onNavigateToRequestBlood
                            )
                        }


                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Filter Section
                        item {
                            BloodRequestFilters(
                                selectedBloodGroup = selectedBloodGroup,
                                onBloodGroupSelected = { selectedBloodGroup = it },
                                bloodGroupExpanded = bloodGroupExpanded,
                                onBloodGroupExpandedChanged = { bloodGroupExpanded = it },
                                bloodGroups = bloodGroups
                            )
                        }


                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        item {
                            TabRow(selectedTabIndex = selectedTabIndex) {
                                Tab(
                                    selected = selectedTabIndex == 0,
                                    onClick = { selectedTabIndex = 0 },
                                    text = { Text("Urgent") }
                                )
                                Tab(
                                    selected = selectedTabIndex == 1,
                                    onClick = { selectedTabIndex = 1 },
                                    text = { Text("All") }
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (selectedTabIndex == 0) {
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
                        } else {
                            if (filteredRequests.isEmpty()) {
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
                                items(filteredRequests) { request ->
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
}


@Composable
fun QuickActionButtons(
    onFindDonors: () -> Unit,
    onHospitals: () -> Unit,
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


@Composable
fun BloodRequestFilters(
    selectedBloodGroup: String?,
    onBloodGroupSelected: (String?) -> Unit,
    bloodGroupExpanded: Boolean,
    onBloodGroupExpandedChanged: (Boolean) -> Unit,
    bloodGroups: List<String>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filters",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Blood Group Filter
            Text(
                text = "Blood Group",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBloodGroupExpandedChanged(true) }
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedBloodGroup ?: "All Blood Groups",
                        color = if (selectedBloodGroup == null)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        imageVector = if (bloodGroupExpanded)
                            Icons.Default.ExpandLess
                        else
                            Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }
            }
            DropdownMenu(
                expanded = bloodGroupExpanded,
                onDismissRequest = { onBloodGroupExpandedChanged(false) },
                modifier = Modifier.fillMaxWidth()
            ) {
                DropdownMenuItem(
                    text = { Text("All Blood Groups") },
                    onClick = {
                        onBloodGroupSelected(null)
                        onBloodGroupExpandedChanged(false)
                    }
                )
                bloodGroups.forEach { group ->
                    DropdownMenuItem(
                        text = { Text(group) },
                        onClick = {
                            onBloodGroupSelected(group)
                            onBloodGroupExpandedChanged(false)
                        }
                    )
                }
            }
        }
    }
}





