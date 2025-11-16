package com.example.okoablood.ui.screens.donors

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.okoablood.data.model.Appointment
import com.example.okoablood.data.model.Donor
import com.example.okoablood.data.model.User
import com.example.okoablood.data.repository.AppointmentDataSource
import com.example.okoablood.data.repository.BloodDonationRepositoryImpl
import com.example.okoablood.data.repository.DonorDataSource
import com.example.okoablood.data.repository.UserDataSource
import com.example.okoablood.ui.components.*
import com.example.okoablood.viewmodel.DonorViewModel
import com.example.okoablood.viewmodel.DonorViewModel.DonorsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllDonorsScreen(
    viewModel: DonorViewModel,
    onDonorSelected: (String) -> Unit,
    onSearchDonors: () -> Unit,
    onBack: () -> Unit,
) {
    val donorState by viewModel.donorsState.collectAsState()
    
    // Filter state
    var selectedBloodGroup by remember { mutableStateOf<String?>(null) }
    var showUrgentOnly by remember { mutableStateOf(false) }
    var bloodGroupExpanded by remember { mutableStateOf(false) }
    
    val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    Scaffold(
        topBar = {
            OkoaBloodTopAppBar(
                title = "Find Donors",
                showBackButton = true,
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
                            .clickable { bloodGroupExpanded = true }
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = MaterialTheme.shapes.small
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
                        onDismissRequest = { bloodGroupExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Blood Groups") },
                            onClick = {
                                selectedBloodGroup = null
                                bloodGroupExpanded = false
                                viewModel.setBloodGroupFilter(null)
                            }
                        )
                        bloodGroups.forEach { group ->
                            DropdownMenuItem(
                                text = { Text(group) },
                                onClick = {
                                    selectedBloodGroup = group
                                    bloodGroupExpanded = false
                                    viewModel.setBloodGroupFilter(group)
                                }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Urgency Filter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Show Urgent Only",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Donors needed for urgent requests",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Switch(
                            checked = showUrgentOnly,
                            onCheckedChange = {
                                showUrgentOnly = it
                                viewModel.setUrgencyFilter(it)
                            }
                        )
                    }
                }
            }
            
            // Donor List
            when (val state = donorState) {
                is DonorsState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingIndicator()
                    }
                }
                is DonorsState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorMessage(state.message)
                    }
                }
                is DonorsState.Success -> {
                    if (state.donors.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            EmptyStateMessage("No donors found")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            item {
                                Text(
                                    text = "Nearby Donors (${state.donors.size})",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            items(state.donors) { donor ->
                                DonorCard(
                                    donor = donor,
                                    onClick = { onDonorSelected(donor.id) }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
