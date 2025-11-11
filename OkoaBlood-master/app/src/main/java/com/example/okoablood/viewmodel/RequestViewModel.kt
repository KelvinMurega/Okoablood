package com.example.okoablood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.okoablood.data.model.BloodRequest
import com.example.okoablood.data.repository.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RequestViewModel(
    private val requestRepository: RequestRepository) : ViewModel() {

    private val _createRequestState = MutableStateFlow<CreateRequestState>(CreateRequestState.Idle)
    val createRequestState: StateFlow<CreateRequestState> = _createRequestState

    fun createBloodRequest(request: BloodRequest) {
        viewModelScope.launch {
            _createRequestState.value = CreateRequestState.Loading
            try {
                val result = requestRepository.createBloodRequest(request)
                _createRequestState.value = if (result.isSuccess) {
                    CreateRequestState.Success(result.getOrNull())
                } else {
                    CreateRequestState.Error(result.exceptionOrNull()?.message ?: "Failed to create request")
                }
            } catch (e: Exception) {
                _createRequestState.value = CreateRequestState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun resetState() {
        _createRequestState.value = CreateRequestState.Idle
    }

    sealed class CreateRequestState {
        object Idle : CreateRequestState()
        object Loading : CreateRequestState()
        data class Success(val requestId: String?) : CreateRequestState()
        data class Error(val message: String) : CreateRequestState()
    }
}