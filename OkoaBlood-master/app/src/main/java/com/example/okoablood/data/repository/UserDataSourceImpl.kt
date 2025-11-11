package com.example.okoablood.data.datasource.impl

import com.example.okoablood.data.model.User
import com.example.okoablood.data.model.BloodRequest
import com.example.okoablood.data.remote.FirebaseService
import com.example.okoablood.data.repository.UserDataSource

class UserDataSourceImpl(private val firebaseService: FirebaseService) : UserDataSource {
    override suspend fun createUserProfile(user: User): Result<Void?> {
        return try {
            firebaseService.createUserProfile(user)
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }}
    override suspend fun getUserProfile(userId: String): User {
        return firebaseService.getUser(userId)
            ?: throw IllegalStateException("User not found for ID: $userId")
    }

    override suspend fun updateUserProfile(user: User): User {
        firebaseService.updateUser(user)
        return user
    }

    override suspend fun getBloodRequestsByUser(userId: String): Result<List<BloodRequest>> {
        return try {
            val requests = firebaseService.getBloodRequestsByUser(userId)
            Result.success(requests)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
