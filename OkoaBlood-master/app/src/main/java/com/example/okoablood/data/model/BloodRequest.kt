package com.example.okoablood.data.model

import java.util.Date

data class BloodRequest(
    val id: String = "",
    val requesterId: String? = null,
    val requesterName: String? = null,
    val requesterPhoneNumber: String = "",
    val patientName: String = "",
    val bloodGroup: String = "",
    val units: Int? = null,
    val hospital: String = "",
    val location: String = "",
    val constituency: String? = null,
    val urgent: Boolean = false,
    val urgencyLevel: String? = null, // "Low", "Medium", "High", "Critical"
    val additionalInfo: String? =null,
    val status: String = "Active",
    val requestDate: Long = System.currentTimeMillis()
)