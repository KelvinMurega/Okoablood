package com.example.okoablood.data.model

import java.time.LocalDate
import java.time.LocalTime

data class Appointment(
    val id: String,
    val userId: String,
    val date: LocalDate,
    val time: LocalTime,
    val location: String,
    val status: AppointmentStatus,
    val donor: Donor?
) {
    enum class AppointmentStatus {
        PENDING,
        CONFIRMED,
        COMPLETED,
        CANCELLED
    }
}
