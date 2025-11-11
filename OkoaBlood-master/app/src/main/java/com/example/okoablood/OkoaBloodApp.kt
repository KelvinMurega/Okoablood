//package com.example.okoablood
//
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Favorite
//import androidx.compose.material.icons.filled.Home
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material.icons.outlined.Favorite
//import androidx.compose.material.icons.outlined.Home
//import androidx.compose.material.icons.outlined.Person
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.NavigationBar
//import androidx.compose.material3.NavigationBarItem
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.navigation.NavDestination.Companion.hierarchy
//import androidx.navigation.NavGraph.Companion.findStartDestination
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.currentBackStackEntryAsState
//import androidx.navigation.compose.rememberNavController
//import com.example.okoablood.ui.screens.DonateScreen
//import com.example.okoablood.ui.screens.HomeScreen
//import com.example.okoablood.ui.screens.LoginScreen
//import com.example.okoablood.ui.screens.ProfileScreen
//import com.example.okoablood.ui.screens.RegisterScreen
//import com.example.okoablood.ui.screens.RequestBloodScreen
//import com.example.okoablood.ui.screens.RequestDetailsScreen
//import com.example.okoablood.ui.screens.SplashScreen
//
//sealed class Screen(val route: String) {
//    data object Splash : Screen("splash")
//    data object Login : Screen("login")
//    data object Register : Screen("register")
//    data object Home : Screen("home")
//    data object Donate : Screen("donate")
//    data object Profile : Screen("profile")
//    data object RequestBlood : Screen("request_blood")
//    data object RequestDetails : Screen("request_details/{requestId}") {
//        fun createRoute(requestId: String) = "request_details/$requestId"
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun OkoaBloodApp(
//    navController: NavHostController = rememberNavController(),
//    startDestination: String = Screen.Splash.route
//) {
//    val bottomNavItems = listOf(
//        BottomNavItem(
//            route = Screen.Home.route,
//            selectedIcon = Icons.Filled.Home,
//            unselectedIcon = Icons.Outlined.Home,
//            title = "Home"
//        ),
//        BottomNavItem(
//            route = Screen.Donate.route,
//            selectedIcon = Icons.Filled.Favorite,
//            unselectedIcon = Icons.Outlined.Favorite,
//            title = "Donate"
//        ),
//        BottomNavItem(
//            route = Screen.Profile.route,
//            selectedIcon = Icons.Filled.Person,
//            unselectedIcon = Icons.Outlined.Person,
//            title = "Profile"
//        )
//    )
//
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentDestination = navBackStackEntry?.destination
//    val showBottomBar = currentDestination?.route in listOf(
//        Screen.Home.route,
//        Screen.Donate.route,
//        Screen.Profile.route
//    )
//
//    Scaffold(
//        bottomBar = {
//            if (showBottomBar) {
//                NavigationBar {
//                    bottomNavItems.forEach { item ->
//                        val selected = currentDestination?.hierarchy?.any {
//                            it.route == item.route
//                        } == true
//
//                        NavigationBarItem(
//                            icon = {
//                                Icon(
//                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
//                                    contentDescription = item.title
//                                )
//                            },
//                            label = {
//                                Text(
//                                    text = item.title,
//                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
//                                )
//                            },
//                            selected = selected,
//                            onClick = {
//                                navController.navigate(item.route) {
//                                    popUpTo(navController.graph.findStartDestination().id) {
//                                        saveState = true
//                                    }
//                                    launchSingleTop = true
//                                    restoreState = true
//                                }
//                            }
//                        )
//                    }
//                }
//            }
//        }
//    ) { innerPadding ->
//        NavHost(
//            navController = navController,
//            startDestination = startDestination,
//            modifier = Modifier.padding(innerPadding)
//        ) {
//            composable(Screen.Splash.route) {
//                SplashScreen(
//                    onNavigateToLogin = {
//                        navController.navigate(Screen.Login.route) {
//                            popUpTo(Screen.Splash.route) { inclusive = true }
//                        }
//                    }
//                )
//            }
//
//            composable(Screen.Login.route) {
//                LoginScreen(
//                    onLoginSuccess = {
//                        navController.navigate(Screen.Home.route) {
//                            popUpTo(Screen.Login.route) { inclusive = true }
//                        }
//                    },
//                    onNavigateToRegister = {
//                        navController.navigate(Screen.Register.route)
//                    }
//                )
//            }
//
//            composable(Screen.Register.route) {
//                RegisterScreen(
//                    onRegisterSuccess = {
//                        navController.navigate(Screen.Home.route) {
//                            popUpTo(Screen.Register.route) { inclusive = true }
//                        }
//                    },
//                    onNavigateToLogin = {
//                        navController.popBackStack()
//                    }
//                )
//            }
//
//            composable(Screen.Home.route) {
//                HomeScreen(
//                    onNavigateToRequestDetails = { requestId ->
//                        navController.navigate(Screen.RequestDetails.createRoute(requestId))
//                    },
//                    onNavigateToRequestBlood = {
//                        navController.navigate(Screen.RequestBlood.route)
//                    }
//                )
//            }
//
//            composable(Screen.Donate.route) {
//                DonateScreen()
//            }
//
//            composable(Screen.Profile.route) {
//                ProfileScreen(
//                    onLogout = {
//                        navController.navigate(Screen.Login.route) {
//                            popUpTo(navController.graph.id) { inclusive = true }
//                        }
//                    }
//                )
//            }
//
//            composable(Screen.RequestBlood.route) {
//                RequestBloodScreen(
//                    onRequestSubmitted = {
//                        navController.popBackStack()
//                    },
//                    onNavigateBack = {
//                        navController.popBackStack()
//                    }
//                )
//            }
//
//            composable(Screen.RequestDetails.route) { backStackEntry ->
//                val requestId = backStackEntry.arguments?.getString("requestId") ?: ""
//                RequestDetailsScreen(
//                    requestId = requestId,
//                    onNavigateBack = {
//                        navController.popBackStack()
//                    }
//                )
//            }
//        }
//    }
//}
//
//data class BottomNavItem(
//    val route: String,
//    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
//    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
//    val title: String
//)
