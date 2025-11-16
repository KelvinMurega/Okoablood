package com.example.okoablood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.okoablood.data.model.Donor
import com.example.okoablood.data.model.BloodRequest
import com.example.okoablood.data.remote.FirebaseService
import com.example.okoablood.data.repository.BloodDonationRepository
import com.example.okoablood.data.repository.BloodDonationRepositoryImpl
import com.example.okoablood.di.DependencyProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DonorViewModel(
    val firebaseService: FirebaseService = DependencyProvider.firebaseService,
    val repository: BloodDonationRepository = DependencyProvider.repository,
    val bloodRequestViewModel: BloodRequestViewModel = BloodRequestViewModel(firebaseService, repository)


) : ViewModel() {

    private val _donorsState = MutableStateFlow<DonorsState>(DonorsState.Loading)
    val donorsState: StateFlow<DonorsState> = _donorsState
    
    private val _allDonors = MutableStateFlow<List<Donor>>(emptyList())
    private val _urgentBloodGroups = MutableStateFlow<Set<String>>(emptySet())
    
    // Filter states
    var selectedBloodGroup: String? = null
        private set
    var showUrgentOnly: Boolean = false
        private set

    init {
        // Replace wuth actual location
        searchNearbyDonors("Nairobi")
        loadUrgentBloodGroups()
    }
    
    private fun loadUrgentBloodGroups() {
        viewModelScope.launch {
            try {
                val urgentRequests = repository.getUrgentRequests()
                val urgentGroups = urgentRequests.map { it.bloodGroup }.toSet()
                _urgentBloodGroups.value = urgentGroups
            } catch (e: Exception) {
                // Silently fail - urgency filter will just not work
                _urgentBloodGroups.value = emptySet()
            }
        }
    }

    fun searchDonorsByBloodGroup(bloodGroup: String) {
        viewModelScope.launch {
            _donorsState.value = DonorsState.Loading
            try {
                val donors = repository.getDonorsByBloodGroup(bloodGroup)
                _allDonors.value = donors
                applyFilters()
            } catch (e: Exception) {
                _donorsState.value = DonorsState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun searchNearbyDonors(location: String) {
        viewModelScope.launch {
            _donorsState.value = DonorsState.Loading
            try {
                val donors = repository.getNearbyDonors(location)
                println("Searching nearby donors at location: $location")
                println("Fetched donors: ${donors.size}")
                _allDonors.value = donors
                applyFilters()
            } catch (e: Exception) {
                _donorsState.value = DonorsState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun setBloodGroupFilter(bloodGroup: String?) {
        selectedBloodGroup = bloodGroup
        applyFilters()
    }
    
    fun setUrgencyFilter(urgentOnly: Boolean) {
        showUrgentOnly = urgentOnly
        applyFilters()
    }
    
    private fun applyFilters() {
        viewModelScope.launch {
            var filteredDonors = _allDonors.value
            
            // Apply blood group filter
            if (selectedBloodGroup != null && selectedBloodGroup!!.isNotBlank()) {
                filteredDonors = filteredDonors.filter { it.bloodGroup == selectedBloodGroup }
            }
            
            // Apply urgency filter
            if (showUrgentOnly) {
                val urgentGroups = _urgentBloodGroups.value
                filteredDonors = filteredDonors.filter { it.bloodGroup in urgentGroups }
            }
            
            _donorsState.value = DonorsState.Success(filteredDonors)
        }
    }


    sealed class DonorsState {
        object Loading : DonorsState()
        data class Success(val donors: List<Donor>) : DonorsState()
        data class Error(val message: String) : DonorsState()
    }
}
