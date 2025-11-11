package com.example.okoablood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.okoablood.data.model.Donor
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

    init {
        // Replace wuth actual location
        searchNearbyDonors("Nairobi")
    }

    fun searchDonorsByBloodGroup(bloodGroup: String) {
        viewModelScope.launch {
            _donorsState.value = DonorsState.Loading
            try {
                val donors = repository.getDonorsByBloodGroup(bloodGroup)
                _donorsState.value = DonorsState.Success(donors)
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
                _donorsState.value = DonorsState.Success(donors)
            } catch (e: Exception) {
                _donorsState.value = DonorsState.Error(e.message ?: "Unknown error")
            }
        }}


    sealed class DonorsState {
        object Loading : DonorsState()
        data class Success(val donors: List<Donor>) : DonorsState()
        data class Error(val message: String) : DonorsState()
    }
}
