package com.example.okoablood.data.repository

import com.example.okoablood.data.model.BloodRequest
import com.example.okoablood.data.model.User

interface UserDataSource {
    suspend fun updateUserProfile(user: User): User
    suspend fun getUserProfile(userId: String): User
    suspend fun getBloodRequestsByUser(userId: String): Result<List<BloodRequest>>
    suspend fun createUserProfile(user: User): Result<Void?>

}
