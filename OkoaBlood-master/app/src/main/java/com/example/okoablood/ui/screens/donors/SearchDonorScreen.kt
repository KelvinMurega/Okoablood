package com.example.okoablood.ui.screens.donors

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDonorScreen(
    viewModel: DonorViewModel,
    onBack: () -> Unit
) {
    var searchBloodGroup by remember { mutableStateOf("") }
    val donorState by viewModel.donorsState.collectAsState()

    Scaffold(
        topBar = {
            OkoaBloodTopAppBar(title = "Search Donors", onBack = onBack)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchBloodGroup,
                onValueChange = {
                    searchBloodGroup = it
                    if (it.isNotBlank()) {
                        viewModel.searchDonorsByBloodGroup(it)
                    }
                },
                label = { Text("Search by Blood Group") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = donorState) {
                is DonorViewModel.DonorsState.Loading -> LoadingIndicator()
                is DonorViewModel.DonorsState.Error -> ErrorMessage(state.message)
                is DonorViewModel.DonorsState.Success -> {
                    if (state.donors.isEmpty()) {
                        EmptyStateMessage("No donors found")
                    } else {
                        LazyColumn {
                            items(state.donors) { donor ->
                                DonorCard(donor = donor, onClick = { println("Selected ${donor.name}") })
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
