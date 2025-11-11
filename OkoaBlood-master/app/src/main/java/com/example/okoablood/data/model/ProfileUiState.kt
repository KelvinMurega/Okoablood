package com.example.okoablood.data.model

data class ProfileUiState(
    val isLoading: Boolean = false,
    val userProfile: User? = User.EMPTY,
    val userAppointments: List<Appointment> = emptyList(),
    val userRequests: List<BloodRequest> = emptyList(),
    val error: String? = null,
    val donorRegistrationState: DonorRegistrationState = DonorRegistrationState.Idle
) {
    sealed class DonorRegistrationState {
        object Idle : DonorRegistrationState()
        object Success : DonorRegistrationState()
        data class Error(val message: String) : DonorRegistrationState()
    }
}
