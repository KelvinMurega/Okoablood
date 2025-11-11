package com.example.okoablood.ui.screens.donors

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

      Scaffold(
        topBar = {
            OkoaBloodTopAppBar(
                title = "Find Donors",
                showBackButton = true,
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = donorState) {
                is DonorsState.Loading -> LoadingIndicator()
                is DonorsState.Error -> ErrorMessage(state.message)
                is DonorsState.Success -> {
                    if (state.donors.isEmpty()) {
                        EmptyStateMessage("No donors found nearby")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            item {
                                Text(
                                    text = "Nearby Donors",
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
