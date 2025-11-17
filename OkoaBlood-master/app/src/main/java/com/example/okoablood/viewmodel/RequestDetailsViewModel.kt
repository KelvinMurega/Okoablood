package com.example.okoablood.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.okoablood.data.model.BloodRequest
import com.example.okoablood.data.repository.BloodDonationRepository
import com.example.okoablood.di.DependencyProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RequestDetailsViewModel(
    val id: String,
    private val repository: BloodDonationRepository = DependencyProvider.repository
) : ViewModel() {

    private val _request = MutableStateFlow<BloodRequest?>(null)
    val request: StateFlow<BloodRequest?> = _request

    init {
        loadRequest(id)
    }

    internal fun loadRequest(id1: String) {
        viewModelScope.launch {
            try {
                val result = repository.getRequestById(id1)
                _request.value = result
            } catch (e: Exception) {
                e.printStackTrace()
                _request.value = null // or show error state
            }
        }
    }}
