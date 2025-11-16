package com.example.okoablood.di

import com.example.okoablood.data.remote.FirebaseService
import com.example.okoablood.data.repository.BloodDonationRepository
import com.example.okoablood.data.repository.BloodDonationRepositoryImpl
import com.example.okoablood.data.repository.RequestRepositoryImpl
import com.example.okoablood.ui.viewmodels.AuthViewModel
import com.example.okoablood.ui.viewmodels.RequestDetailsViewModel
import com.example.okoablood.viewmodel.BloodRequestViewModel
import com.example.okoablood.viewmodel.DonorViewModel
import com.example.okoablood.viewmodel.HomeViewModel
import com.example.okoablood.viewmodel.MapViewModel
import com.example.okoablood.viewmodel.ProfileViewModel

object DependencyProvider {

    lateinit var firebaseService: FirebaseService
    lateinit var repository: BloodDonationRepository

    private val requestRepository by lazy {
        RequestRepositoryImpl(firebaseService)
    }

    fun provideBloodRequestViewModel() = BloodRequestViewModel(firebaseService, repository)
    fun provideRequestDetailsViewModel(requestId: String): RequestDetailsViewModel {
        return RequestDetailsViewModel(requestId, repository)
    }
    fun provideDonorViewModel() = DonorViewModel(firebaseService, repository)
    fun provideAuthViewModel() = AuthViewModel(firebaseService, repository)
    fun provideProfileViewModel() = ProfileViewModel(firebaseService, repository)

    fun provideHomeViewModel() = HomeViewModel(
        firebaseService = firebaseService,
        requestRepository = requestRepository,
        repository = repository
    )
    
    fun provideMapViewModel() = MapViewModel(repository)
}
