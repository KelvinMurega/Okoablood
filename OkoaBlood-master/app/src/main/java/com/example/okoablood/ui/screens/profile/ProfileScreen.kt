package com.example.okoablood.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.okoablood.R
import com.example.okoablood.data.model.User
// --- IMPORT DONOR MODEL ---
import com.example.okoablood.data.model.Donor
import com.example.okoablood.navigation.Routes
import com.example.okoablood.ui.components.OkoaBloodTopAppBar
import com.example.okoablood.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onBack: () -> Unit,
    viewModel: ProfileViewModel,
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadUserProfile()
    }

    Scaffold(
        topBar = {
            OkoaBloodTopAppBar(
                title = "Profile",
                showBackButton = false,
                onBack = onBack,
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.EDIT_PROFILE) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile"
                        )
                    }
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.userProfile != null) {
                ProfileContent(
                    user = uiState.userProfile!!,
                    viewModel = viewModel,
                    navController = navController
                )
            } else {
                Text("Could not load profile. Please try again.")
                Button(onClick = { viewModel.loadUserProfile() }) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
fun ProfileContent(
    user: User,
    viewModel: ProfileViewModel,
    navController: NavHostController
) {
    val eligibility = user.checkDonationEligibility()

    Image(
        painter = painterResource(id = R.drawable.ic_profile), // A default profile icon
        contentDescription = "Profile Picture",
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = user.name,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )

    Text(
        text = user.email,
        style = MaterialTheme.typography.bodyLarge,
        color = Color.Gray
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Donation Eligibility Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Donation Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (eligibility.isEligible) {
                    "✓ Eligible to Donate"
                } else {
                    "⏳ Not Eligible Yet"
                },
                style = MaterialTheme.typography.headlineSmall,
                color = if (eligibility.isEligible) Color(0xFF006400) else MaterialTheme.colorScheme.error
            )

            if (eligibility.isEligible) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (user.lastDonationDate.isNullOrEmpty()) "Ready for your first donation!" else "90 days have passed since your last donation.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        navController.navigate(Routes.SCHEDULE_DONATION)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Log a New Donation")
                }
            } else if (!user.isDonor) {
                // Show if they aren't a donor yet
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Register as a donor to become eligible.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            } else {
                // Show if they are a donor but not eligible
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${eligibility.daysRemaining} days remaining until next donation.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // --- DONOR REGISTRATION CARD ---
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Donor Registration",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (user.isDonor) {
                Text(
                    text = "✓ You are a registered donor.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF006400),
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "Help save lives by becoming a visible donor in the app.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        // --- THIS IS THE NEW LOGIC ---
                        // 1. Create a Donor object from the User details
                        val newDonor = Donor(
                            id = user.id,
                            name = user.name,
                            bloodGroup = user.bloodGroup ?: "N/A",
                            location = user.location,
                            phoneNumber = user.phoneNumber,
                          //  email = user.email,
                            isAvailable = true // Default to available
                        )
                        // 2. Call the ViewModel function
                        viewModel.registerAsDonor(newDonor)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    // Disable button if user hasn't added their blood group
                    enabled = !user.bloodGroup.isNullOrEmpty()
                ) {
                    Text("Register as a Donor")
                }

                if (user.bloodGroup.isNullOrEmpty()) {
                    Text(
                        text = "Please edit your profile to add a blood group before registering.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
    // ------------------------------------

    Spacer(modifier = Modifier.height(16.dp))

    // Profile Details Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ProfileDetailRow(label = "Blood Group", value = user.bloodGroup ?: "N/A")
            ProfileDetailRow(label = "Phone Number", value = user.phoneNumber)
            ProfileDetailRow(label = "Location", value = user.location)
            ProfileDetailRow(label = "Last Donation", value = if (user.lastDonationDate.isNullOrEmpty()) "N/A" else user.lastDonationDate!!)
        }
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}