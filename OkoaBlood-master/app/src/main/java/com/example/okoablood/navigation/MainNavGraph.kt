package com.example.okoablood.navigation

import android.content.Intent
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.okoablood.ui.components.*
import com.example.okoablood.ui.screens.notifications.NotificationsScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.okoablood.data.model.Donor
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
import kotlinx.coroutines.launch


object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"

    const val HOME = "home"
    const val PROFILE = "profile"
    const val NOTIFICATIONS = "notifications"

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
                        popUpTo(0) { inclusive = true }
                    }
                },
                isLoggedIn = isLoggedIn
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
            MainScreenWithNavigation(
                navController = navController,
                homeViewModel = homeViewModel,
                currentRoute = Routes.HOME
            )
        }

        composable(Routes.ALL_DONORS) {
            MainScreenWithNavigation(
                navController = navController,
                homeViewModel = homeViewModel,
                currentRoute = Routes.ALL_DONORS
            )
        }

        composable(Routes.NOTIFICATIONS) {
            MainScreenWithNavigation(
                navController = navController,
                homeViewModel = homeViewModel,
                currentRoute = Routes.NOTIFICATIONS
            )
        }

        composable(Routes.PROFILE) {
            MainScreenWithNavigation(
                navController = navController,
                homeViewModel = homeViewModel,
                currentRoute = Routes.PROFILE
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenWithNavigation(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    currentRoute: String
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Drawer items
    val drawerItems = listOf(
        DrawerItem(
            title = "Share App",
            icon = Icons.Default.Share,
            onClick = {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "OkoaBlood App")
                    putExtra(Intent.EXTRA_TEXT, "Join me in saving lives with OkoaBlood! Download the app now.")
                }
                context.startActivity(Intent.createChooser(intent, "Share via"))
            }
        ),
        DrawerItem(
            title = "About Us",
            icon = Icons.Default.Info,
            onClick = {
                // TODO: Navigate to About Us screen when implemented
                Toast.makeText(context, "About Us coming soon!", Toast.LENGTH_SHORT).show()
            }
        ),
        DrawerItem(
            title = "Partners",
            icon = Icons.Default.Business,
            onClick = {
                // TODO: Navigate to Partners screen when implemented
                Toast.makeText(context, "Partners coming soon!", Toast.LENGTH_SHORT).show()
            }
        )
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            OkoaBloodNavigationDrawer(
                drawerState = drawerState,
                items = drawerItems,
                onClose = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            bottomBar = {
                // Show bottom nav only on main screens
                if (currentRoute in listOf(
                        Routes.HOME,
                        Routes.ALL_DONORS,
                        Routes.NOTIFICATIONS,
                        Routes.PROFILE
                    )
                ) {
                    OkoaBloodBottomNavigation(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            when (currentRoute) {
                Routes.HOME -> {
                    val bloodRequestViewModel = DependencyProvider.provideBloodRequestViewModel()
                    HomeScreen(
                        viewModel = homeViewModel,
                        onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
                        onNavigateToDonors = { navController.navigate(Routes.ALL_DONORS) },
                        onNavigateToRequests = { navController.navigate(Routes.ALL_REQUESTS) },
                        onNavigateToRequestDetails = { requestId ->
                            navController.navigateToRequestDetails(requestId)
                        },
                        onNavigateToRequestBlood = { navController.navigate(Routes.NEW_REQUESTS) },
                        onNavigateToNotifications = { navController.navigate(Routes.NOTIFICATIONS) },
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        bloodRequestViewModel = bloodRequestViewModel
                    )
                }
                Routes.ALL_DONORS -> {
                    val donorViewModel = DependencyProvider.provideDonorViewModel()
                    AllDonorsScreen(
                        viewModel = donorViewModel,
                        onBack = { navController.popBackStack() },
                        onDonorSelected = { donorId -> navController.navigateToDonorDetails(donorId) },
                        onSearchDonors = { navController.navigate(Routes.SEARCH_DONORS) },
                    )
                }
                Routes.NOTIFICATIONS -> {
                    NotificationsScreen(
                        onBack = { navController.popBackStack() },
                        onRequestClick = { requestId ->
                            navController.navigateToRequestDetails(requestId)
                        }
                    )
                }
                Routes.PROFILE -> {
                    val viewModel = remember { DependencyProvider.provideProfileViewModel() }
                    ProfileScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onLogout = {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
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
