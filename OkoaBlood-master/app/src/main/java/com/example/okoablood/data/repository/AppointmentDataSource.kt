package com.example.okoablood.data.repository

import com.example.okoablood.data.model.Appointment

interface AppointmentDataSource {
    suspend fun getUserAppointments(userId: String): List<Appointment>
}
