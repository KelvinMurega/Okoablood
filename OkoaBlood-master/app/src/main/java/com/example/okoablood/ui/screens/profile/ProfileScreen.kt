package com.example.okoablood.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.okoablood.viewmodel.ProfileViewModel
import com.example.okoablood.ui.components.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onBack: () -> Unit,
    viewModel: ProfileViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        viewModel.loadUserProfile()
        viewModel.loadUserRequests()
    }

    LaunchedEffect(uiState.userProfile) {
        uiState.userProfile?.let {
            name = it.name
            phoneNumber = it.phoneNumber
            location = it.location
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                        onLogout()
                    }
                ) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            OkoaBloodTopAppBar(
                title = "Profile",
                showBackButton = true,
                onBack = onBack,
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }
            }

            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error: ${uiState.error}")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            viewModel.loadUserProfile()
                            viewModel.loadUserRequests()
                        }) {
                            Text("Retry")
                        }
                    }
                }
            }

            uiState.userProfile == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateMessage("Profile information not available.")
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = uiState.userProfile?.name?.firstOrNull()?.uppercase() ?: "U",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = uiState.userProfile?.name ?: "",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Blood Type: ${uiState.userProfile!!.bloodGroup}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            val isDonor = uiState.userProfile?.isDonor == true

                            Text(
                                text = if (isDonor) "Donor: Yes" else "Donor: No",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            if (!isDonor) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        uiState.userProfile?.let { user ->
                                            val updatedUser = user.copy(isDonor = true)
                                            viewModel.updateProfile(updatedUser)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Become a Donor")
                                }
                            } else {
                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Thank you for being a donor!",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )



                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Last Donation: ${uiState.userProfile?.lastDonationDate ?: "Not available"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }




                            Spacer(modifier = Modifier.height(16.dp))


                            OkoaBloodButton(
                                text = if (isEditing) "Save Profile" else "Edit Profile",
                                onClick = {
                                    if (isEditing) {
                                        uiState.userProfile?.let { currentUser ->
                                            val updatedUser = currentUser.copy(
                                                name = name,
                                                phoneNumber = phoneNumber,
                                                location = location
                                            )
                                            viewModel.updateProfile(updatedUser)
                                        }
                                    }

                                    isEditing = !isEditing
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            SectionHeader(title = "Profile Information")

                            Spacer(modifier = Modifier.height(8.dp))
                            OkoaBloodTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = "Full Name",
                                isEnabled = isEditing,
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            OkoaBloodTextField(
                                value = uiState.userProfile?.email?:"",
                                onValueChange = {},
                                label = "Email",
                                isEnabled = false
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            OkoaBloodTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                label = "Phone Number",
                                isEnabled = isEditing
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            OkoaBloodTextField(
                                value = location,
                                onValueChange = { location = it },
                                label = "Location",
                                isEnabled = isEditing
                            )

                        }
                    }

                    if (uiState.userRequests.isNotEmpty()) {
                        item {
                            SectionHeader(title = "My Blood Requests")
                        }
                        items(uiState.userRequests) { request ->
                            BloodRequestCard(
                                bloodRequest = request,
                                onClick = { /* handle click */ }
                            )
                        }
                    }
                }
            }
        }
    }
}
