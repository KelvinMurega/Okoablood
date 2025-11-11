package com.example.okoablood.ui.screens.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.okoablood.data.model.BloodRequest
import com.example.okoablood.ui.components.BloodRequestCard
import com.example.okoablood.ui.components.EmptyStateMessage
import com.example.okoablood.ui.components.OkoaBloodTopAppBar
import java.util.Date

// TODO: Replace with Firebase data fetching
// This is placeholder data for UI development
private val placeholderUrgentRequests = listOf(
    BloodRequest(
        id = "1",
        patientName = "John Doe",
        bloodGroup = "O+",
        hospital = "Kenyatta National Hospital",
        location = "Nairobi",
        urgent = true,
        requestDate = System.currentTimeMillis() - 3600000, // 1 hour ago
        requesterPhoneNumber = "+254712345678"
    ),
    BloodRequest(
        id = "2",
        patientName = "Jane Smith",
        bloodGroup = "A-",
        hospital = "Aga Khan Hospital",
        location = "Mombasa",
        urgent = true,
        requestDate = System.currentTimeMillis() - 7200000, // 2 hours ago
        requesterPhoneNumber = "+254723456789"
    ),
    BloodRequest(
        id = "3",
        patientName = "Michael Johnson",
        bloodGroup = "B+",
        hospital = "Nairobi Hospital",
        location = "Nairobi",
        urgent = true,
        requestDate = System.currentTimeMillis() - 10800000, // 3 hours ago
        requesterPhoneNumber = "+254734567890"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    onRequestClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO: Replace with ViewModel and StateFlow from Firebase
    val urgentRequests = remember { placeholderUrgentRequests }

    Scaffold(
        topBar = {
            OkoaBloodTopAppBar(
                title = "Urgent Requests",
                onBack = null,
                showBackButton = false
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (urgentRequests.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "No urgent requests",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        EmptyStateMessage(
                            message = "No urgent donation requests at the moment.\nCheck back later!"
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "Urgent blood donation requests",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "These requests need immediate attention",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    items(urgentRequests) { request ->
                        BloodRequestCard(
                            bloodRequest = request,
                            onClick = { onRequestClick(request.id) }
                        )
                    }
                }
            }
        }
    }
}

