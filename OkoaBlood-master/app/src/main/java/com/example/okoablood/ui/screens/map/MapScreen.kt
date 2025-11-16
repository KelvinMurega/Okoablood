package com.example.okoablood.ui.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.okoablood.ui.components.ErrorMessage
import com.example.okoablood.ui.components.LoadingIndicator
import com.example.okoablood.ui.components.OkoaBloodTopAppBar
import com.example.okoablood.viewmodel.MapViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

// Nairobi center coordinates
private val NAIROBI_CENTER = LatLng(-1.2921, 36.8219)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onRequestClick: (String) -> Unit = {}
) {
    val hospitalMarkers by viewModel.hospitalMarkers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(NAIROBI_CENTER, 11f)
    }
    
    // Initialize map properties with error handling
    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = false
            )
        )
    }
    
    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = true,
                compassEnabled = true,
                myLocationButtonEnabled = false
            )
        )
    }

    Scaffold(
        topBar = {
            OkoaBloodTopAppBar(
                title = "Blood Requests Map",
                showBackButton = false,
                actions = {
                    IconButton(onClick = { viewModel.loadBloodRequests() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    LoadingIndicator()
                }
                error != null -> {
                    ErrorMessage(error ?: "Unknown error")
                }
                else -> {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = mapProperties,
                        uiSettings = mapUiSettings
                    ) {
                        // Add markers for each hospital
                        hospitalMarkers.forEach { marker ->
                            Marker(
                                state = MarkerState(position = LatLng(marker.latitude, marker.longitude)),
                                title = marker.hospitalName,
                                snippet = "${marker.requestCount} blood request(s)",
                                onClick = {
                                    // Handle marker click - could show info window or navigate
                                    false // Return false to show default info window
                                }
                            )
                        }
                    }
                    
                    // Legend/Info Card
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Blood Requests by Hospital",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            if (hospitalMarkers.isEmpty()) {
                                Text(
                                    text = "No active blood requests found",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            } else {
                                Text(
                                    text = "${hospitalMarkers.size} hospital(s) with active requests",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                // Show top hospitals
                                hospitalMarkers
                                    .sortedByDescending { it.requestCount }
                                    .take(3)
                                    .forEach { marker ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.Red),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "${marker.requestCount}",
                                                    color = Color.White,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = marker.hospitalName,
                                                style = MaterialTheme.typography.bodySmall,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                            }
                        }
                    }
                }
            }
        }
    }
}

