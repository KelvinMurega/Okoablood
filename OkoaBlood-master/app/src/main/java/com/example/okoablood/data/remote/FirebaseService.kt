package com.example.okoablood.data.remote

import android.util.Log
import com.example.okoablood.data.model.Appointment
import com.example.okoablood.data.model.AuthUser
import com.example.okoablood.data.model.BloodRequest
import com.example.okoablood.data.model.Donor
import com.example.okoablood.data.model.User
import com.example.okoablood.data.repository.AppointmentDataSource
import com.example.okoablood.data.repository.DonorDataSource
import com.example.okoablood.data.repository.UserDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID

open class FirebaseService {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    internal val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()


    private val usersCollection = firestore.collection("users")
    private val donorsCollection = firestore.collection("donors")
    private val requestsCollection = firestore.collection("bloodRequests")


    // Aliased Authentication methods
    open suspend fun signIn(email: String, password: String): AuthUser? {
        val user = auth.signInWithEmailAndPassword(email, password).await().user
        return user?.let { AuthUser(uid = it.uid, email = it.email) }
    }
open suspend fun signUp(email: String, password: String): AuthUser? {
        val user = auth.createUserWithEmailAndPassword(email, password).await().user
        return user?.let { AuthUser(uid = it.uid, email = it.email) }
    }

    open fun signOut() {
        auth.signOut()
    }

    open fun getCurrentUser(): AuthUser? {
        val user = auth.currentUser
        return user?.let { AuthUser(uid = it.uid, email = it.email) }
    }

    // User operations
    open suspend fun createUser(user: User): String {
        val uid = user.id
        usersCollection.document(uid).set(user).await()
        return uid
    }

    open suspend fun getUser(userId: String): User? {
        val snapshot = usersCollection.document(userId).get().await()
        return if (snapshot.exists()) {
            snapshot.toObject(User::class.java)
        } else {
            null
        }
    }


    open suspend fun updateUser(user: User) {
        usersCollection.document(user.id).set(user).await()
    }

    // Donor operations
    open suspend fun getAllDonors(): List<Donor> {
        val snapshot = donorsCollection.get().await()
        return snapshot.documents.mapNotNull { it.toObject(Donor::class.java) }
    }

    open suspend fun getDonorsByBloodGroup(bloodGroup: String): List<Donor> {
        val snapshot = donorsCollection
            .whereEqualTo("bloodGroup", bloodGroup)
            .whereEqualTo("isAvailable", true)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(Donor::class.java) }
    }

    open suspend fun getDonor(userId: String): Donor? {
        val snapshot = donorsCollection.document(userId).get().await()
        return snapshot.toObject(Donor::class.java)
    }

    // Blood Request operations
    suspend fun getRequestById(id: String): BloodRequest? {
        return firestore.collection("bloodRequests")
            .document(id)
            .get()
            .await()
            .toObject(BloodRequest::class.java)
    }

    open suspend fun createBloodRequest(request: BloodRequest): String {
        val id = UUID.randomUUID().toString()
        val newRequest = request.copy(id = id)
        requestsCollection.document(id).set(newRequest).await()
        return id
    }

    open suspend fun getAllBloodRequests(): List<BloodRequest> {
        val snapshot = requestsCollection
            .whereEqualTo("status", "Active")
            .orderBy("requestDate")
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(BloodRequest::class.java) }
    }

    open suspend fun getBloodRequestsByUser(userId: String): List<BloodRequest> {
        val snapshot = requestsCollection
            .whereEqualTo("requestedBy", userId)
            .orderBy("createdAt")
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(BloodRequest::class.java) }
    }

    open suspend fun updateBloodRequestStatus(requestId: String, status: String) {
        requestsCollection.document(requestId).update("status", status).await()
    }

    // Optional: Observe operations (use flow to simulate)
    open fun observeAllDonors(): Flow<List<Donor>> = flow {
        emit(getAllDonors())
    }

    open fun observeActiveBloodRequests(): Flow<List<BloodRequest>> = flow {
        emit(getAllBloodRequests())
    }

    private val db = FirebaseFirestore.getInstance()

    open suspend fun getUrgentRequests(): List<BloodRequest> {
        val snapshot = db.collection("bloodRequests")
            .whereEqualTo("urgent", true)
            .get()
            .await()

        // Logging
        Log.d("FirebaseService", "Fetched ${snapshot.size()} urgent blood requests")

        val requests = snapshot.toObjects(BloodRequest::class.java)
        Log.d("FirebaseService", "Parsed urgent requests: $requests")

        return requests
    }



    open suspend fun getAllRequests(): List<BloodRequest> {
        val snapshot = db.collection("bloodRequests")
            .get()
            .await()

        // Logging
        Log.d("FirebaseService", "Fetched ${snapshot.size()} total blood requests")

        val requests = snapshot.toObjects(BloodRequest::class.java)
        Log.d("FirebaseService", "Parsed all requests: $requests")

        return requests
    }

    open suspend fun createUserProfile(user: User): Result<Void?> {
        return try {
            usersCollection.document(user.id).set(user).await()
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    open suspend fun registerDonor(donor: Donor) {
        donorsCollection.document(donor.id).set(donor).await()
    }

    suspend fun getUserAppointments(userId: String): List<Appointment> {
        return firestore.collection("appointments")
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .toObjects(Appointment::class.java)
    }
}