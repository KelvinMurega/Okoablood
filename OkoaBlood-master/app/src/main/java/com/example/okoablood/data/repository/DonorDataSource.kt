package com.example.okoablood.data.repository

import com.example.okoablood.data.model.Donor

interface DonorDataSource {
    suspend fun registerDonor(donor: Donor): Donor
    suspend fun getDonorDetails(donorId: String): Donor
    suspend fun getNearbyDonors(location: String): List<Donor>

}
