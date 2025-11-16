package com.example.okoablood.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.okoablood.ui.components.OkoaBloodTopAppBar
import com.example.okoablood.viewmodel.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDonationScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit,
    onDonationScheduled: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.userProfile

    // Fetch the profile as soon as the screen is composed
    LaunchedEffect(key1 = Unit) {
        viewModel.loadUserProfile() // <-- RENAMED 'fetchProfile' to 'loadUserProfile'
    }

    Scaffold(
        topBar = {
            OkoaBloodTopAppBar(
                title = "Log Donation",
                showBackButton = true,
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Confirm Your Donation",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Format today's date for confirmation
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val todayDate = sdf.format(Date(System.currentTimeMillis()))

            Text(
                text = "This will log your donation for today: $todayDate",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (user != null) {
                        // Create the updated user object with the new donation date
                        val updatedUser = user.copy(
                            lastDonationDate = todayDate
                        )
                        // Call the ViewModel to update the profile
                        viewModel.updateProfile(updatedUser)
                        onDonationScheduled()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                // Only enable the button if the user profile is loaded
                enabled = user != null
            ) {
                Text("Confirm and Log Donation")
            }
        }
    }
}