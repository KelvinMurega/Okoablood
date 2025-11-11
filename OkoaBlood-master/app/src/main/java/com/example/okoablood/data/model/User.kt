package com.example.okoablood.data.model

import java.util.Date

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

