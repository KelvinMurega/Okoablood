package com.example.okoablood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.okoablood.data.model.BloodRequest
import com.example.okoablood.data.model.HomeUiState
import com.example.okoablood.data.remote.FirebaseService
import com.example.okoablood.data.repository.BloodDonationRepository
import com.example.okoablood.data.repository.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

class HomeViewModel(
    private val requestRepository: RequestRepository,
    private val repository: BloodDonationRepository,
    private val firebaseService: FirebaseService
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadBloodRequests()
    }

    fun loadBloodRequests() {
        viewModelScope.launch {
            _uiState.value = HomeUiState(isLoading = true)

            try {
                val allRequests = mutableListOf<BloodRequest>()

                // Collect urgent requests - handle Flow exceptions properly
                val urgentResult = try {
                    requestRepository.getUrgentBloodRequests().first()
                } catch (e: CancellationException) {
                    // Re-throw cancellation exceptions (they're part of coroutine cancellation)
                    throw e
                } catch (e: Exception) {
                    Result.failure<List<BloodRequest>>(e)
                }
                
                if (urgentResult.isSuccess) {
                    allRequests.addAll(urgentResult.getOrDefault(emptyList()))
                } else {
                    _uiState.value = HomeUiState(
                        isLoading = false,
                        error = urgentResult.exceptionOrNull()?.message ?: "Failed to load urgent requests"
                    )
                    return@launch
                }

                // Collect active requests - handle Flow exceptions properly
                val activeResult = try {
                    requestRepository.getActiveBloodRequests().first()
                } catch (e: CancellationException) {
                    // Re-throw cancellation exceptions (they're part of coroutine cancellation)
                    throw e
                } catch (e: Exception) {
                    Result.failure<List<BloodRequest>>(e)
                }
                
                if (activeResult.isSuccess) {
                    allRequests.addAll(activeResult.getOrDefault(emptyList()))
                    _uiState.value = HomeUiState(
                        isLoading = false,
                        bloodRequests = allRequests
                    )
                } else {
                    _uiState.value = HomeUiState(
                        isLoading = false,
                        error = activeResult.exceptionOrNull()?.message ?: "Failed to load active requests"
                    )
                }

            } catch (e: Exception) {
                _uiState.value = HomeUiState(
                    isLoading = false,
                    error = e.message ?: "Unexpected error occurred"
                )
            }
        }
    }
}
