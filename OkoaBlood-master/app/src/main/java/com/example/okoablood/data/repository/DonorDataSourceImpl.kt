package com.example.okoablood.data.datasource.impl

import com.example.okoablood.data.model.Donor
import com.example.okoablood.data.remote.FirebaseService
import com.example.okoablood.data.repository.DonorDataSource

class DonorDataSourceImpl(private val firebaseService: FirebaseService) : DonorDataSource {

    override suspend fun registerDonor(donor: Donor): Donor {
        firebaseService.registerDonor(donor)
        return donor
    }


    override suspend fun getDonorDetails(donorId: String): Donor {
        return firebaseService.getDonor(donorId)
            ?: throw IllegalStateException("Donor not found for ID: $donorId")
    }
    override suspend fun getNearbyDonors(location: String): List<Donor> {
        // Just return a dummy list or real Firestore query based on location
        return listOf(
            Donor(id = "1", name = "Alice Blood", bloodGroup = "O+", location = "Nairobi", phoneNumber = "0712345678"),
            Donor(id = "2", name = "Bob Plasma", bloodGroup = "A-", location = "Nairobi", phoneNumber = "0723456789")
        )    }

}
