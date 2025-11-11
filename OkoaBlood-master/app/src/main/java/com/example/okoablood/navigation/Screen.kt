package com.example.okoablood.navigation

// File: app/src/main/java/com/example/okoablood/navigation/Screen.kt

import androidx.navigation.NavBackStackEntry

sealed class Screen(val route: String) {
    // Auth Flow
    object Splash : Screen("splash")
    object Login : Screen("login") {
        fun route() = route
    }
    object Register : Screen("register") {
        fun route() = route
    }

    // Main Flow
    object Home : Screen("home") {
        fun route() = route
    }
    object Profile : Screen("profile/{${NavArguments.USER_ID}}") {
        fun route(userId: String) = "profile/$userId"
    }

    // Donor Flow
    object DonorList : Screen("donor_list?${NavArguments.BLOOD_GROUP}={${NavArguments.BLOOD_GROUP}}&${NavArguments.LOCATION}={${NavArguments.LOCATION}}") {
        fun route(
            bloodType: String? = null,
            location: String? = null
        ) = "donor_list?" +
                "${NavArguments.BLOOD_GROUP}=${bloodType ?: ""}&" +
                "${NavArguments.LOCATION}=${location ?: ""}"
    }

    object DonorDetail : Screen("donor_detail/{${NavArguments.DONOR_ID}}") {
        fun route(donorId: String) = "donor_detail/$donorId"
    }

    object SearchDonor : Screen("search_donor") {
        fun route() = route
    }

    // Requests Flow
    object NewBloodRequest : Screen("new_blood_request") {
        fun route() = route
    }

    object AllRequests : Screen("all_requests") {
        fun route() = route
    }

    // Argument parsers
    companion object {
        fun getDonorId(backStackEntry: NavBackStackEntry): String {
            return requireNotNull(backStackEntry.arguments?.getString(NavArguments.DONOR_ID)) {
                "Donor ID is required"
            }
        }

        fun getUserId(backStackEntry: NavBackStackEntry): String {
            return requireNotNull(backStackEntry.arguments?.getString(NavArguments.USER_ID)) {
                "User ID is required"
            }
        }

        fun getBloodTypeFilter(backStackEntry: NavBackStackEntry): String? {
            return backStackEntry.arguments?.getString(NavArguments.BLOOD_GROUP)
        }

        fun getLocationFilter(backStackEntry: NavBackStackEntry): String? {
            return backStackEntry.arguments?.getString(NavArguments.LOCATION)
        }
    }
}