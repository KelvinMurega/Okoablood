package com.example.okoablood.data.model

import java.util.Date

data class Donor(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val bloodGroup: String = "",
    val location: String = "",
    val profileImageUrl: String = "",
    val isAvailable : Boolean = true,
    val availableForDonation: Boolean = true,
    val lastDonationDate: Long = 0

)