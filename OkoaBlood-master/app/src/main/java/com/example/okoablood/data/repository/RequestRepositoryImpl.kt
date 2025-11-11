package com.example.okoablood.data.repository

import com.example.okoablood.data.model.BloodRequest
import com.example.okoablood.data.remote.FirebaseService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RequestRepositoryImpl(
    private val firebaseService: FirebaseService
) : RequestRepository {

    override fun getUrgentBloodRequests(): Flow<Result<List<BloodRequest>>> = flow {
        try {
            val requests = firebaseService.getUrgentRequests()
            emit(Result.success(requests))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getActiveBloodRequests(): Flow<Result<List<BloodRequest>>> = flow {
        try {
            val requests = firebaseService.getAllRequests()
            emit(Result.success(requests))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun createBloodRequest(request: BloodRequest): Result<String> {
        return try {
            val requestId = firebaseService.createBloodRequest(request)
            Result.success(requestId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getAllBloodRequests(): List<BloodRequest> {
        return firebaseService.getAllRequests()
    }

    override fun observeActiveBloodRequests(): Flow<List<BloodRequest>> {
        return firebaseService.observeActiveBloodRequests()
    }

    override fun getRequestById(id: String): Flow<BloodRequest?> = flow {
        try {
            val allRequests = firebaseService.getAllRequests()
            val request = allRequests.find { it.id == id }
            emit(request)
        } catch (e: Exception) {
            emit(null)
        }
    }
}
