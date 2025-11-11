package com.example.okoablood.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.okoablood.data.model.BloodRequest
import com.example.okoablood.data.remote.FirebaseService
import com.example.okoablood.data.repository.BloodDonationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.Result
import androidx.compose.runtime.State



class BloodRequestViewModel(
    private val firebaseService: FirebaseService,
    private val repository: BloodDonationRepository
) : ViewModel() {

    private val _bloodRequestsState = MutableStateFlow<BloodRequestsState>(BloodRequestsState.Loading)
    val bloodRequestsState: StateFlow<BloodRequestsState> = _bloodRequestsState
    private val _submitRequestState = mutableStateOf<Result<String>?>(null)
    val submitRequestState: State<Result<String>?> = _submitRequestState

    fun createBloodRequest(request: BloodRequest) {
        viewModelScope.launch {
            _submitRequestState.value = null
            try {
                val result = repository.createBloodRequest(request)
                _submitRequestState.value = Result.success(result)
            } catch (e: Exception) {
                _submitRequestState.value = Result.failure(e)
            }
        }
    }



    fun clearSubmitRequestState() {
        _submitRequestState.value = null
    }

    private val _filteredBloodRequestsState = MutableStateFlow<BloodRequestsState>(BloodRequestsState.Loading)
    val filteredBloodRequestsState: StateFlow<BloodRequestsState> = _filteredBloodRequestsState

    fun loadAllBloodRequests() {
        viewModelScope.launch {
            _bloodRequestsState.value = BloodRequestsState.Loading
            try {
                val bloodRequests = repository.getAllBloodRequests()
                _bloodRequestsState.value = BloodRequestsState.Success(bloodRequests)
            } catch (e: Exception) {
                _bloodRequestsState.value = BloodRequestsState.Error("Failed to load requests: ${e.message}")
            }
        }
    }


    fun filterBloodRequests(query: String) {
        viewModelScope.launch {
            _filteredBloodRequestsState.value = BloodRequestsState.Loading
            try {
                val filteredRequests = repository.getAllBloodRequests().filter {
                    it.bloodGroup.contains(query, ignoreCase = true)
                }
                _filteredBloodRequestsState.value = BloodRequestsState.Success(filteredRequests)
            } catch (e: Exception) {
                _filteredBloodRequestsState.value = BloodRequestsState.Error("Failed to filter requests: ${e.message}")
            }
        }
    }

    sealed class BloodRequestsState {
        object Loading : BloodRequestsState()
        data class Success(val requests: List<BloodRequest>) : BloodRequestsState()
        data class Error(val message: String) : BloodRequestsState()
    }
}
