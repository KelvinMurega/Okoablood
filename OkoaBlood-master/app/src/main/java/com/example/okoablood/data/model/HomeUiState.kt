package com.example.okoablood.data.model

data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val bloodRequests: List<BloodRequest> = emptyList()
)