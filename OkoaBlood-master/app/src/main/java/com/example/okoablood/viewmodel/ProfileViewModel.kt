package com.example.okoablood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.okoablood.data.model.Donor
import com.example.okoablood.data.model.ProfileUiState
import com.example.okoablood.data.model.User
import com.example.okoablood.data.remote.FirebaseService
import com.example.okoablood.data.repository.BloodDonationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val firebaseService: FirebaseService,
    private val repository: BloodDonationRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState

    /**
     * Computed property that returns the donation eligibility status for the current user
     */
    val donationEligibility: User.DonationEligibility
        get() = _uiState.value.userProfile?.checkDonationEligibility() 
            ?: User.DonationEligibility(isEligible = false, daysRemaining = 0)

    fun loadUserProfile(retries: Int = 2) {
        val currentUserId = firebaseService.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repeat(retries) { attempt ->
                val userResult = repository.getUserProfile(currentUserId)
                if (userResult.isSuccess) {
                    val appointmentsResult = repository.getUserAppointments(currentUserId)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        userProfile = userResult.getOrNull(),
                        userAppointments = appointmentsResult.getOrNull() ?: emptyList(),
                        error = null
                    )
                    // Load requests after user profile successfully loaded
                    loadUserRequests(currentUserId)
                    return@launch
                }
            }
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "User not found for id: $currentUserId"
            )
        }
    }

    fun loadUserRequests(userId: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(error = null)
            val uid = userId ?: firebaseService.getCurrentUser()?.uid
            if (uid == null) {
                _uiState.value = _uiState.value.copy(
                    error = "User not authenticated."
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val requests = repository.getBloodRequestsByUser(uid)
                _uiState.value = _uiState.value.copy(
                    userRequests = requests,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load user requests"
                )
            }

        }
    }

    fun updateProfile(user: User) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.updateUserProfile(user)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userProfile = user,
                    error = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to update profile"
                )
            }
        }
    }

    fun registerAsDonor(donor: Donor) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.registerDonor(donor)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isLoading = false,
                    donorRegistrationState = ProfileUiState.DonorRegistrationState.Success,
                    error = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    donorRegistrationState = ProfileUiState.DonorRegistrationState.Error("Registration failed"),
                    error = null
                )
            }
        }
    }

    fun logout() {
        firebaseService.signOut()
    }
}
