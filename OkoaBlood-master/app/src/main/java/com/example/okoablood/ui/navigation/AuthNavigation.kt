//package com.example.okoablood.ui.navigation
//
//
//import android.annotation.SuppressLint
//import androidx.compose.runtime.Composable
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.example.okoablood.ui.screens.LoginScreen
//import com.example.okoablood.ui.screens.RegisterScreen
//import com.example.okoablood.ui.viewmodels.AuthViewModel
//
//@SuppressLint("StateFlowValueCalledInComposition")
//@Composable
//fun AuthNavigation(
//    navController: NavHostController = rememberNavController(),
//    viewModel: AuthViewModel,
//    onAuthSuccess: () -> Unit
//) {
//    NavHost(
//        navController = navController,
//        startDestination = "login"
//    ) {
//        composable("login") {
//            LoginScreen(
//                navController = navController,
//                viewModel = viewModel,
//            onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
//            )
//        }
//
//        composable("register") {
//            RegisterScreen(
//                navController = navController,
//                viewModel = viewModel,
//                onNavigateToLogin = { navController.navigate("login") {
//                    popUpTo("login") { inclusive = true }
//                }}
//            )
//        }
//
//        // You can add more auth-related screens here if needed
//        // For example, password reset, email verification, etc.
//    }
//
//    // Observe authentication state and navigate accordingly
//    if (viewModel.authState.value is AuthViewModel.AuthState.Authenticated) {
//        onAuthSuccess()
//    }
//}