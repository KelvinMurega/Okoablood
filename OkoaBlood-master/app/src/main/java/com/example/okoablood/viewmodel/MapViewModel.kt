package com.example.okoablood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.okoablood.data.model.BloodRequest
import com.example.okoablood.data.repository.BloodDonationRepository
import com.example.okoablood.di.DependencyProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HospitalMarker(
    val hospitalName: String,
    val latitude: Double,
    val longitude: Double,
    val requestCount: Int,
    val requests: List<BloodRequest>
)

class MapViewModel(
    private val repository: BloodDonationRepository = DependencyProvider.repository
) : ViewModel() {

    private val _hospitalMarkers = MutableStateFlow<List<HospitalMarker>>(emptyList())
    val hospitalMarkers: StateFlow<List<HospitalMarker>> = _hospitalMarkers

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadBloodRequests()
    }

    fun loadBloodRequests() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val requests = repository.getAllBloodRequests()
                val groupedByHospital = requests
                    .filter { it.hospital.isNotBlank() }
                    .groupBy { it.hospital.trim() }
                
                val markers = groupedByHospital.mapNotNull { (hospitalName, hospitalRequests) ->
                    val location = getHospitalLocation(hospitalName)
                    location?.let {
                        HospitalMarker(
                            hospitalName = hospitalName,
                            latitude = it.first,
                            longitude = it.second,
                            requestCount = hospitalRequests.size,
                            requests = hospitalRequests
                        )
                    }
                }
                
                _hospitalMarkers.value = markers
            } catch (e: Exception) {
                _error.value = "Failed to load blood requests: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Maps hospital names to coordinates in Nairobi
     * This is a simplified mapping - in production, you'd use geocoding
     */
    private fun getHospitalLocation(hospitalName: String): Pair<Double, Double>? {
        val normalizedName = hospitalName.lowercase().trim()
        
        // Nairobi center coordinates
        val nairobiCenter = Pair(-1.2921, 36.8219)
        
        // Common Nairobi hospitals with approximate coordinates
        val hospitalLocations = mapOf(
            "kenyatta national hospital" to Pair(-1.3014, 36.7897),
            "knh" to Pair(-1.3014, 36.7897),
            "mater misericordiae hospital" to Pair(-1.2800, 36.8000),
            "mater hospital" to Pair(-1.2800, 36.8000),
            "nairobi hospital" to Pair(-1.2731, 36.8122),
            "aga khan university hospital" to Pair(-1.2600, 36.8000),
            "aga khan hospital" to Pair(-1.2600, 36.8000),
            "mp shah hospital" to Pair(-1.2700, 36.8100),
            "gertrude's children's hospital" to Pair(-1.2800, 36.8200),
            "gertrudes hospital" to Pair(-1.2800, 36.8200),
            "nairobi west hospital" to Pair(-1.2900, 36.7800),
            "coptic hospital" to Pair(-1.2750, 36.8150),
            "karen hospital" to Pair(-1.3200, 36.7000),
            "nairobi women's hospital" to Pair(-1.2850, 36.8050),
            "nairobi womens hospital" to Pair(-1.2850, 36.8050),
            "chiromo lane hospital" to Pair(-1.2700, 36.8000),
            "ladies medical centre" to Pair(-1.2750, 36.8100),
            "the nairobi hospital" to Pair(-1.2731, 36.8122)
        )
        
        // Try exact match first
        hospitalLocations[normalizedName]?.let { return it }
        
        // Try partial match
        hospitalLocations.keys.firstOrNull { normalizedName.contains(it) || it.contains(normalizedName) }?.let {
            return hospitalLocations[it]!!
        }
        
        // If no match found, return a random location near Nairobi center with slight offset
        // In production, use geocoding API
        val offset = (normalizedName.hashCode() % 100) / 1000.0
        return Pair(
            nairobiCenter.first + offset,
            nairobiCenter.second + offset
        )
    }
}

