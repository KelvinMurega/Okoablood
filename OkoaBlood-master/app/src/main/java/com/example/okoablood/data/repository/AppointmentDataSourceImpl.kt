package com.example.okoablood.data.repository

import com.example.okoablood.data.model.Appointment
import com.example.okoablood.data.remote.FirebaseService

class AppointmentDataSourceImpl(
    private val firebaseService: FirebaseService
) : AppointmentDataSource {

    override suspend fun getUserAppointments(userId: String): List<Appointment> {
        return try {
            firebaseService.getUserAppointments(userId)
        } catch (e: Exception) {
            emptyList() // or log the error if needed
        }
    }
}