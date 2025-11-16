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

    // --- THIS FUNCTION HAS BEEN UPDATED ---
    fun registerAsDonor(donor: Donor) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            // 1. Register the Donor in 'donors' collection
            val result = repository.registerDonor(donor)

            if (result.isSuccess) {
                // 2. Get the current user
                val currentUser = _uiState.value.userProfile
                if (currentUser != null) {
                    // 3. Create an updated user with isDonor = true
                    val updatedUser = currentUser.copy(isDonor = true)

                    // 4. Call your existing updateProfile function to save the user
                    updateProfile(updatedUser)

                    // 5. Update UI state (updateProfile will handle this)
                    _uiState.value = _uiState.value.copy(
                        donorRegistrationState = ProfileUiState.DonorRegistrationState.Success
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to update user profile after donor registration."
                    )
                }
            } else {
                // Donor registration failed
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    donorRegistrationState = ProfileUiState.DonorRegistrationState.Error("Registration failed"),
                    error = "Donor registration failed"
                )
            }
        }
    }

    fun logout() {
        firebaseService.signOut()
    }
}