package com.example.okoablood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.okoablood.data.datasource.impl.DonorDataSourceImpl
import com.example.okoablood.data.datasource.impl.UserDataSourceImpl
import com.example.okoablood.data.remote.FirebaseService
import com.example.okoablood.data.repository.AppointmentDataSourceImpl
import com.example.okoablood.data.repository.BloodDonationRepositoryImpl
import com.example.okoablood.di.DependencyProvider
import com.example.okoablood.navigation.MainNavGraph // <-- IMPORT ADDED
import com.example.okoablood.navigation.Routes // <-- IMPORT ADDED
import com.example.okoablood.ui.theme.OkoaBloodTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize FirebaseService (already singleton via DependencyProvider)
        val firebaseService = FirebaseService()

        // Initialize DataSources
        val userDataSource = UserDataSourceImpl(firebaseService)
        val donorDataSource = DonorDataSourceImpl(firebaseService)
        val appointmentDataSource = AppointmentDataSourceImpl(firebaseService)

        // Initialize Repository
        val repository = BloodDonationRepositoryImpl(
            firebaseService = firebaseService,
            userDataSource = userDataSource,
            donorDataSource = donorDataSource,
            appointmentDataSource = appointmentDataSource
        )

        // Inject into DependencyProvider (optional but recommended)
        DependencyProvider.firebaseService = firebaseService
        DependencyProvider.repository = repository

        setContent {
            OkoaBloodTheme {
                val navController = rememberNavController()
                val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
                // Always start with SplashScreen
                val startDestination = Routes.SPLASH

                MainNavGraph(
                    navController = navController,
                    isLoggedIn = isLoggedIn,
                    startDestination = startDestination
                )
            }
        }
    }
}