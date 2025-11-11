package com.example.okoablood.data.repository

import com.example.okoablood.data.model.Appointment
import com.example.okoablood.data.model.BloodRequest
import com.example.okoablood.data.model.Donor
import com.example.okoablood.data.model.User
import com.example.okoablood.data.remote.FirebaseService
import com.example.okoablood.data.model.AuthUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repository class that handles all data operations related to users, donors,
 * appointments, and blood requests by delegating to FirebaseService and data sources.
 */
open class BloodDonationRepository(
    private val firebaseService: FirebaseService,
    private val userDataSource: UserDataSource,
    private val donorDataSource: DonorDataSource,
    private val appointmentDataSource: AppointmentDataSource
) {

    // -------------------------
    // AUTHENTICATION
    // -------------------------
    open suspend fun signIn(email: String, password: String): AuthUser? {
        return firebaseService.signIn(email, password)
    }

    open suspend fun signUp(email: String, password: String): AuthUser? {
        return firebaseService.signUp(email, password)
    }

    open fun signOut() {
        firebaseService.signOut()
    }

    open fun getCurrentUser(): AuthUser? {
        return firebaseService.getCurrentUser()
    }

    // -------------------------
    // USER OPERATIONS
    // -------------------------
    open suspend fun createUser(user: User): String {
        return firebaseService.createUser(user)
    }

    open suspend fun getUser(userId: String): User? {
        return firebaseService.getUser(userId)
    }

    open suspend fun updateUser(user: User): Boolean {
        firebaseService.updateUser(user)
        return true
    }

    suspend fun updateUserProfile(user: User): Result<User> {
        return try {
            val updatedUser = withContext(Dispatchers.IO) {
                userDataSource.updateUserProfile(user)
            }
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(userId: String): Result<User> {
        return try {
            val userProfile = withContext(Dispatchers.IO) {
                userDataSource.getUserProfile(userId)
            }
            Result.success(userProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    open suspend fun getUserAppointments(userId: String): Result<List<Appointment>> {
        return try {
            val appointments = withContext(Dispatchers.IO) {
                appointmentDataSource.getUserAppointments(userId)
            }
            Result.success(appointments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // -------------------------
    // DONOR OPERATIONS
    // -------------------------
    open suspend fun getAllDonors(): List<Donor> {
        return firebaseService.getAllDonors()
    }
    open suspend fun getNearbyDonors(location: String): List<Donor> {
        return donorDataSource.getNearbyDonors(location)
    }

    open suspend fun getDonorsByBloodGroup(bloodGroup: String): List<Donor> {
        return firebaseService.getDonorsByBloodGroup(bloodGroup)
    }

    open suspend fun getDonor(userId: String): Donor? {
        return firebaseService.getDonor(userId)
    }

    open suspend fun registerDonor(donor: Donor): Result<Donor> {
        return try {
            val registeredDonor = withContext(Dispatchers.IO) {
                donorDataSource.registerDonor(donor)
            }
            Result.success(registeredDonor)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // -------------------------
    // BLOOD REQUEST OPERATIONS
    // -------------------------
    // BloodDonationRepositoryImpl.kt
    open suspend fun getRequestById(id: String): BloodRequest? {
        return firebaseService.getRequestById(id)  // or your custom logic
    }

    open suspend fun createBloodRequest(request: BloodRequest): String {
        return firebaseService.createBloodRequest(request)
    }

    open suspend fun getAllBloodRequests(): List<BloodRequest> {
        return firebaseService.getAllBloodRequests()
    }
    open suspend fun getUrgentRequests(): List<BloodRequest> {
        return firebaseService.getUrgentRequests()
    }

    open suspend fun getBloodRequestsByUser(userId: String): List<BloodRequest> {
        return withContext(Dispatchers.IO) {
            firebaseService.getBloodRequestsByUser(userId)
        }
    }
    open suspend fun updateBloodRequestStatus(requestId: String, status: String) {
        firebaseService.updateBloodRequestStatus(requestId, status)
    }

    // -------------------------
    // REAL-TIME OBSERVERS
    // -------------------------
    open fun observeAllDonors(): Flow<List<Donor>> {
        return firebaseService.observeAllDonors()
    }

    open fun observeActiveBloodRequests(): Flow<List<BloodRequest>> {
        return firebaseService.observeActiveBloodRequests()
    }

}
