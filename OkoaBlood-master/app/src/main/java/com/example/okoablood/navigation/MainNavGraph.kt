package com.example.okoablood.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.okoablood.data.datasource.impl.DonorDataSourceImpl
import com.example.okoablood.data.datasource.impl.UserDataSourceImpl
import com.example.okoablood.data.model.Donor
import com.example.okoablood.data.remote.FirebaseService
import com.example.okoablood.data.repository.AppointmentDataSource
import com.example.okoablood.data.repository.AppointmentDataSourceImpl
import com.example.okoablood.data.repository.BloodDonationRepositoryImpl
import com.example.okoablood.data.repository.DonorDataSource
import com.example.okoablood.data.repository.UserDataSource
import com.example.okoablood.di.DependencyProvider
import com.example.okoablood.ui.screen.AllRequestsScreen
import com.example.okoablood.ui.screen.BloodRequestsScreen
import com.example.okoablood.ui.screens.LoginScreen
import com.example.okoablood.ui.screens.RegisterScreen
import com.example.okoablood.ui.screens.donors.AllDonorsScreen
import com.example.okoablood.ui.screens.donors.DonorDetailsScreen
import com.example.okoablood.ui.screens.donors.SearchDonorScreen
import com.example.okoablood.ui.screens.home.HomeScreen
import com.example.okoablood.ui.screens.profile.ProfileScreen
import com.example.okoablood.ui.screens.requests.NewBloodRequestScreen
import com.example.okoablood.ui.screens.requests.RequestDetailsScreen
import com.example.okoablood.ui.screens.splash.SplashScreen
import com.example.okoablood.viewmodel.HomeViewModel


object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"

    const val HOME = "home"
    const val PROFILE = "profile"

    const val ALL_DONORS = "all_donors"
    const val DONOR_DETAILS = "donor_details/{${NavArguments.DONOR_ID}}"
    const val SEARCH_DONORS = "search_donors"

    const val ALL_REQUESTS = "all_requests"
    const val NEW_REQUESTS = "new_requests"
    const val BLOOD_REQUESTS = "bloodrequests"
    const val REQUEST_DETAILS = "request_details/{${NavArguments.REQUEST_ID}}"
}

@Composable
fun MainNavGraph(
    navController: NavHostController,
    isLoggedIn: Boolean,
    startDestination: String = if (isLoggedIn) Routes.HOME else Routes.SPLASH
) {
    val repository = DependencyProvider.repository
    val firebaseService = DependencyProvider.firebaseService
    val homeViewModel = remember { DependencyProvider.provideHomeViewModel() }


    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(0) { inclusive = true } // ✅ Safe
                    }


        }
            )
        }

        composable(Routes.LOGIN) {
            val authViewModel = DependencyProvider.provideAuthViewModel()
            LoginScreen(
                viewModel = authViewModel,
                navController = navController,
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(0) { inclusive = true } // ✅ Safe
                    }


        },
                onNavigateToRegister = {
                    navController.navigate("register")
                }            )
        }

        composable(Routes.REGISTER) {
            val authViewModel = DependencyProvider.provideAuthViewModel()
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(0) { inclusive = true } // ✅ Safe
                    }


        },
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN)
                }
                ,
                navController = navController,
                viewModel = authViewModel
            )
        }

        composable(Routes.HOME) {
            // Initialize FirebaseService and DataSources
            val firebaseService = FirebaseService()

            val userDataSource = UserDataSourceImpl(firebaseService)
            val donorDataSource = DonorDataSourceImpl(firebaseService)
            val appointmentDataSource = AppointmentDataSourceImpl(firebaseService)

            // Inject dependencies into the DependencyProvider
            DependencyProvider.firebaseService = firebaseService
            DependencyProvider.repository = BloodDonationRepositoryImpl(
                firebaseService = firebaseService,
                userDataSource = userDataSource,
                donorDataSource = donorDataSource,
                appointmentDataSource = appointmentDataSource
            )
            val bloodRequestViewModel = DependencyProvider.provideBloodRequestViewModel()

            HomeScreen(
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
                onNavigateToDonors = { navController.navigate(Routes.ALL_DONORS) },
                onNavigateToRequests = { navController.navigate(Routes.ALL_REQUESTS) },
                onNavigateToRequestDetails = { requestId ->
                    navController.navigateToRequestDetails(requestId)
                },
                onNavigateToRequestBlood = { navController.navigate(Routes.NEW_REQUESTS) },
                viewModel = homeViewModel,
                bloodRequestViewModel = bloodRequestViewModel
            )
        }

        composable(Routes.PROFILE) {
            val viewModel = remember { DependencyProvider.provideProfileViewModel() }

            ProfileScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate("login") { popUpTo("home") { inclusive = true } }
                })
        }


        composable(Routes.ALL_DONORS) {
            val donorViewModel = DependencyProvider.provideDonorViewModel()
            AllDonorsScreen(
                viewModel = donorViewModel,
                onBack = { navController.popBackStack() },
                onDonorSelected = { donorId -> navController.navigateToDonorDetails(donorId) },
                onSearchDonors = { navController.navigate(Routes.SEARCH_DONORS) },
            )
        }

        composable(Routes.DONOR_DETAILS) { backStackEntry ->
            val donorId = backStackEntry.arguments?.getString(NavArguments.DONOR_ID) ?: ""
            val donorViewModel = DependencyProvider.provideDonorViewModel()
            DonorDetailsScreen(
                donorId = donorId,
                viewModel = donorViewModel,
                onBack = { navController.popBackStack() },
                donor = Donor()
            )
        }

        composable(Routes.SEARCH_DONORS) {
            val donorViewModel = DependencyProvider.provideDonorViewModel()
            SearchDonorScreen(
                viewModel = donorViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ALL_REQUESTS) {
            val bloodRequestViewModel = DependencyProvider.provideBloodRequestViewModel()
            AllRequestsScreen(
                viewModel = bloodRequestViewModel,
                onRequestSelected = { requestId -> navController.navigateToRequestDetails(requestId) },
                onCreateNewRequest = { navController.navigate(Routes.BLOOD_REQUESTS) },
                onBack = { navController.popBackStack() },
                onDonorSelected = { donorId -> navController.navigateToDonorDetails(donorId) }
            )
        }
        composable(Routes.NEW_REQUESTS) {
            val bloodRequestViewModel = DependencyProvider.provideBloodRequestViewModel()
            NewBloodRequestScreen(
                viewModel = bloodRequestViewModel,
                onRequestSubmitted = { navController.popBackStack()
                    homeViewModel.loadBloodRequests()                },
                onBack = { navController.popBackStack() }
            )
        }


        composable(Routes.BLOOD_REQUESTS) {
            val bloodRequestViewModel = DependencyProvider.provideBloodRequestViewModel()
            BloodRequestsScreen(
                viewModel = bloodRequestViewModel,
                onSubmitRequest = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable("request_details/{requestId}") { backStackEntry ->
            val requestId = backStackEntry.arguments?.getString("requestId") ?: ""
            val viewModel = remember(requestId) {
                DependencyProvider.provideRequestDetailsViewModel(requestId)
            }

            RequestDetailsScreen(
                id = requestId,
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

    }
}

// Navigation extension functions
fun NavHostController.navigateToLogin() = navigate(Routes.LOGIN)

fun NavHostController.navigateToHome() = navigate(Routes.HOME) {
    popUpTo(0) { inclusive = true } // ✅ Safe
}


fun NavHostController.navigateToDonorDetails(donorId: String) {
    navigate(Routes.DONOR_DETAILS.replace("{${NavArguments.DONOR_ID}}", donorId))
}

fun NavHostController.navigateToRequestDetails(requestId: String) {
    navigate(Routes.REQUEST_DETAILS.replace("{${NavArguments.REQUEST_ID}}", requestId))
}
fun NavHostController.navigateToLoginAndClearBackStack() = navigate(Routes.LOGIN) {
    popUpTo(0) { inclusive = true }
}
