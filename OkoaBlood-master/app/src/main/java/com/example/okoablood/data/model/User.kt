package com.example.okoablood.data.model

import java.text.SimpleDateFormat
import java.util.*

data class User(
    val userid: String? =null,
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val bloodGroup: String? = "",
    val location: String = "",
    val profileImageUrl: String? = null,
    val isDonor: Boolean = false,
    val lastDonationDate: String? = null,
    val createdAt: Long = System.currentTimeMillis()

){
    /**
     * Data class to represent donation eligibility status
     */
    data class DonationEligibility(
        val isEligible: Boolean,
        val daysRemaining: Int = 0
    )

    /**
     * Checks if the user is eligible to donate blood.
     * A donor must wait 90 days between donations.
     * 
     * @return DonationEligibility with eligibility status and days remaining if not eligible
     */
    fun checkDonationEligibility(): DonationEligibility {
        // If user is not a donor, they are not eligible
        if (!isDonor) {
            return DonationEligibility(isEligible = false, daysRemaining = 0)
        }

        // If no last donation date, user is eligible
        val lastDonation = lastDonationDate ?: return DonationEligibility(isEligible = true)

        // Try to parse the last donation date
        val lastDonationDateParsed = parseDate(lastDonation) ?: return DonationEligibility(isEligible = true)

        // Calculate days since last donation
        val today = Calendar.getInstance()
        val lastDonationCalendar = Calendar.getInstance().apply {
            time = lastDonationDateParsed
        }

        // Calculate difference in days
        val diffInMillis = today.timeInMillis - lastDonationCalendar.timeInMillis
        val daysSinceLastDonation = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()

        // Check if 90 days have passed
        if (daysSinceLastDonation >= 90) {
            return DonationEligibility(isEligible = true)
        } else {
            val daysRemaining = 90 - daysSinceLastDonation
            return DonationEligibility(isEligible = false, daysRemaining = daysRemaining)
        }
    }

    /**
     * Helper function to parse date string in various formats
     */
    private fun parseDate(dateString: String): Date? {
        val dateFormats = listOf(
            "dd MMM yyyy",
            "dd/MM/yyyy",
            "yyyy-MM-dd",
            "MM/dd/yyyy",
            "dd-MM-yyyy"
        )

        for (format in dateFormats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.getDefault())
                sdf.isLenient = false
                return sdf.parse(dateString)
            } catch (e: Exception) {
                // Try next format
            }
        }

        // Try parsing as timestamp (milliseconds)
        try {
            val timestamp = dateString.toLongOrNull()
            if (timestamp != null && timestamp > 0) {
                return Date(timestamp)
            }
        } catch (e: Exception) {
            // Not a timestamp
        }

        return null
    }

companion object {
    val EMPTY = User(
        id = "",
        name = "Unknown",
        email = "",
        phoneNumber = "",
        bloodGroup = "N/A",
        isDonor = false
    )
}}

