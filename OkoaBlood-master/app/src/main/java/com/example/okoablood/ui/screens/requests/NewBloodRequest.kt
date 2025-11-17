package com.example.okoablood.ui.screens.requests


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.okoablood.data.model.BloodRequest
import com.example.okoablood.viewmodel.BloodRequestViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewBloodRequestScreen(
    viewModel: BloodRequestViewModel,
    onRequestSubmitted: () -> Unit,
    onBack: () -> Unit
) {
    // Form state
    var fullName by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf<String?>(null) }
    var units by remember { mutableStateOf("1") }
    var urgencyLevel by remember { mutableStateOf<String?>(null) }
    var hospitalLocation by remember { mutableStateOf("") }
    var constituency by remember { mutableStateOf("") }
    var additionalNotes by remember { mutableStateOf("") }

    var bloodGroupExpanded by remember { mutableStateOf(false) }

    val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    val urgencyLevels = listOf(
        "Low" to "Can wait a few days.",
        "Medium" to "Needed within 48 hours.",
        "High" to "Needed within 24 hours.",
        "Critical" to "Needed immediately."
    )


    val submitResult by viewModel.submitRequestState


    LaunchedEffect(submitResult) {
        if (submitResult?.isSuccess == true) {
            viewModel.clearSubmitRequestState()
            onRequestSubmitted()
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Blood Request") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Post a new blood donation request",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Request Details Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    // Blood drop icon (using a simple colored circle as placeholder)
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Red)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Request Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Fill in the details of your blood request",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Full Name
                Text(
                    text = "Enter Full Name*",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Enter Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Blood Group Dropdown
                Text(
                    text = "Blood Group Needed*",
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
                            text = bloodGroup ?: "Select blood group",
                            color = if (bloodGroup == null)
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
                    bloodGroups.forEach { group ->
                        DropdownMenuItem(
                            text = { Text(group) },
                            onClick = {
                                bloodGroup = group
                                bloodGroupExpanded = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Units Needed
                Text(
                    text = "Units Needed*",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = units,
                    onValueChange = { if (it.all { char -> char.isDigit() }) units = it },
                    label = { Text("Units") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Urgency Level
                Text(
                    text = "Urgency Level*",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    urgencyLevels.forEach { (level, description) ->
                        val isSelected = urgencyLevel == level
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { urgencyLevel = level },
                            shape = RoundedCornerShape(12.dp),
                            border = if (isSelected) {
                                BorderStroke(2.dp, Color.Red)
                            } else {
                                null
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                else
                                    MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = level,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        text = description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { urgencyLevel = level }
                                )
                            }
                        }
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Location Details Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Hospital/Location Name
                Text(
                    text = "Hospital/Location Name*",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = hospitalLocation,
                    onValueChange = { hospitalLocation = it },
                    label = { Text("e.g., Kenyatta National Hospital") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Constituency
                Text(
                    text = "Constituency*",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = constituency,
                    onValueChange = { constituency = it },
                    label = { Text("e.g., Westlands") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Additional Notes
                Text(
                    text = "Additional Notes",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = additionalNotes,
                    onValueChange = { additionalNotes = it },
                    label = { Text("Any additional information about the request...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = {
                    val urgent = urgencyLevel == "High" || urgencyLevel == "Critical"
                    val request = BloodRequest(
                        id = "",
                        patientName = "", // Optional: capture separately if needed
                        requesterName = fullName,
                        bloodGroup = bloodGroup ?: "",
                        units = units.toIntOrNull(),
                        hospital = hospitalLocation,
                        location = hospitalLocation, // Using hospital as location for now
                        constituency = constituency,
                        requesterPhoneNumber = "", // Optional: add a phone field later
                        urgent = urgent,
                        urgencyLevel = urgencyLevel,
                        additionalInfo = additionalNotes.ifBlank { null },
                        requestDate = System.currentTimeMillis()
                    )
                    viewModel.createBloodRequest(request)
                },
                enabled = submitResult == null &&
                        fullName.isNotBlank() &&
                        bloodGroup != null &&
                        units.isNotBlank() &&
                        urgencyLevel != null &&
                        hospitalLocation.isNotBlank() &&
                        constituency.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                Text(
                    text = "Post Blood Request",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Error Message
            submitResult?.exceptionOrNull()?.let {
                Text(
                    text = "Error: ${it.message}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

