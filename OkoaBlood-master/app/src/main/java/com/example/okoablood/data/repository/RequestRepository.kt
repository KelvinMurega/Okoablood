package com.example.okoablood.data.repository

import com.example.okoablood.data.model.BloodRequest
import kotlinx.coroutines.flow.Flow

interface RequestRepository {
    fun getUrgentBloodRequests(): Flow<Result<List<BloodRequest>>>
    fun getActiveBloodRequests(): Flow<Result<List<BloodRequest>>>
    suspend fun createBloodRequest(request: BloodRequest): Result<String>
    fun getRequestById(requestId: String): Flow<BloodRequest?>
    suspend fun getAllBloodRequests(): List<BloodRequest>
    fun observeActiveBloodRequests(): Flow<List<BloodRequest>>
}
