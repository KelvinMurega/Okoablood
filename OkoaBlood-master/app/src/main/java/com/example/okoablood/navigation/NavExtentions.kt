// File: app/src/main/java/com/example/okoablood/navigation/NavExtensions.kt
package com.example.okoablood.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

fun NavController.navigateToScreen(
    screen: Screen,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(screen.route, builder)
}

// Specific navigation helpers
fun NavController.navigateToDonorDetail(donorId: String) {
    navigate(Screen.DonorDetail.route(donorId))
}

fun NavController.navigateToProfile(userId: String) {
    navigate(Screen.Profile.route(userId))
}

fun NavController.navigateToDonorList(
    bloodType: String? = null,
    location: String? = null
) {
    navigate(Screen.DonorList.route(bloodType, location))
}