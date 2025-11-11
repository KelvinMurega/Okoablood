package com.example.okoablood.ui.screens.requests

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
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
    var patientName by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var hospital by remember { mutableStateOf("") }
    var requesterPhoneNumber by remember { mutableStateOf("") }
    var urgent by remember { mutableStateOf(false) }

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
                title = { Text("New Blood Request") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = patientName, onValueChange = { patientName = it }, label = { Text("Patient Name") })
            OutlinedTextField(value = bloodGroup, onValueChange = { bloodGroup = it }, label = { Text("Blood Group") })
            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") })
            OutlinedTextField(value = hospital, onValueChange = { hospital = it }, label = { Text("Hospital") })
            OutlinedTextField(value = requesterPhoneNumber, onValueChange = { requesterPhoneNumber = it }, label = { Text("Phone Number") })

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = urgent, onCheckedChange = { urgent = it })
                Text("Urgent")
            }

            Button(
                onClick = {
                    val request = BloodRequest(
                        id = "", // leave empty; Firestore assigns ID
                        patientName = patientName,
                        bloodGroup = bloodGroup,
                        location = location,
                        hospital = hospital,
                        requesterPhoneNumber = requesterPhoneNumber,
                        urgent = urgent,
                        requestDate = System.currentTimeMillis()
                    )
                    viewModel.createBloodRequest(request)
                },
                enabled = submitResult == null
            ) {
                Text("Submit Request")
            }

            submitResult?.exceptionOrNull()?.let {
                Text("Error: ${it.message}", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
