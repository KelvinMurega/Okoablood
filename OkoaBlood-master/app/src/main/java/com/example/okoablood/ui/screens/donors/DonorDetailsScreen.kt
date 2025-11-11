package com.example.okoablood.ui.screens.donors

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.okoablood.data.model.Donor
import com.example.okoablood.ui.components.OkoaBloodTopAppBar
import com.example.okoablood.viewmodel.DonorViewModel

//import com.example.okoablood.viewmodel.DonorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonorDetailsScreen(
    donor: Donor,
    donorId: String,  // Matches the NavArgument
    onBack: () -> Unit,
  viewModel: DonorViewModel
) {
    Scaffold(
        topBar = {
            OkoaBloodTopAppBar(title = "Donor Details")
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Name: ${donor.name}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Blood Group: ${donor.bloodGroup}")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Phone: ${donor.phoneNumber}")
            // Add more donor details here if needed
        }
    }
}
