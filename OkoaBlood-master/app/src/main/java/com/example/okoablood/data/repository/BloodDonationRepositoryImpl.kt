package com.example.okoablood.data.repository

import com.example.okoablood.data.model.Appointment
import com.example.okoablood.data.model.AuthUser
import com.example.okoablood.data.model.BloodRequest
import com.example.okoablood.data.model.Donor
import com.example.okoablood.data.model.User
import com.example.okoablood.data.remote.FirebaseService
import kotlinx.coroutines.flow.Flow

class BloodDonationRepositoryImpl(
    private val firebaseService: FirebaseService,
    private val donorDataSource: DonorDataSource,
    private val userDataSource: UserDataSource,
    private val appointmentDataSource: AppointmentDataSource
) : BloodDonationRepository(firebaseService, userDataSource, donorDataSource, appointmentDataSource) {

    override suspend fun signIn(email: String, password: String): AuthUser? {
        return firebaseService.signIn(email, password)
    }

    override suspend fun signUp(email: String, password: String): AuthUser? {
        return firebaseService.signUp(email, password)
    }

    override fun signOut() {
        firebaseService.signOut()
    }

    override fun getCurrentUser(): AuthUser? {
        return firebaseService.getCurrentUser()
    }
    suspend fun createUserProfile(user: User): Result<Unit> {
        return try {
            firebaseService.createUserProfile(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun createUser(user: User): String {
        return firebaseService.createUser(user)
    }

    override suspend fun getUser(userId: String): User? {
        return try {
            userDataSource.getUserProfile(userId)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateUser(user: User): Boolean {
        return try {
            userDataSource.updateUserProfile(user)
            true
        } catch (e: Exception) {
            false
        }
    }


    override suspend fun registerDonor(donor: Donor): Result<Donor> {
        return try {
            val registered = donorDataSource.registerDonor(donor)
            Result.success(registered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllDonors(): List<Donor> {
        return firebaseService.getAllDonors()
    }

    override suspend fun getDonorsByBloodGroup(bloodGroup: String): List<Donor> {
        return firebaseService.getDonorsByBloodGroup(bloodGroup)
    }

    override suspend fun getDonor(userId: String): Donor? {
        return try {
            donorDataSource.getDonorDetails(userId)
        } catch (e: Exception) {
            null
        }
    }
    override suspend fun getNearbyDonors(location: String): List<Donor> {
        return donorDataSource.getNearbyDonors(location)
    }

    override suspend fun createBloodRequest(request: BloodRequest): String {
        return firebaseService.createBloodRequest(request)
    }

    override suspend fun getAllBloodRequests(): List<BloodRequest> {
        return firebaseService.getAllBloodRequests()
    }

    override suspend fun getBloodRequestsByUser(userId: String): List<BloodRequest> {
        return userDataSource.getBloodRequestsByUser(userId).getOrElse { emptyList() }
    }

    override suspend fun updateBloodRequestStatus(requestId: String, status: String) {
        firebaseService.updateBloodRequestStatus(requestId, status)
    }

    override suspend fun getRequestById(requestId: String): BloodRequest? {
        return firebaseService.getRequestById(requestId)
    }

    override fun observeAllDonors(): Flow<List<Donor>> {
        return firebaseService.observeAllDonors()
    }

    override fun observeActiveBloodRequests(): Flow<List<BloodRequest>> {
        return firebaseService.observeActiveBloodRequests()
    }

    override suspend fun getUrgentRequests(): List<BloodRequest> {
        return firebaseService.getUrgentRequests()
    }


    override suspend fun getUserAppointments(userId: String): Result<List<Appointment>> {
        return try {
            val appointments = appointmentDataSource.getUserAppointments(userId)
            Result.success(appointments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



}
