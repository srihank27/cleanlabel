package com.example.cleanlabel.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cleanlabel.ui.screens.*
import com.example.cleanlabel.viewmodel.IngredientAnalysisViewModel
import com.example.cleanlabel.viewmodel.LabelAnalysisViewModel
import com.example.cleanlabel.viewmodel.HealthProfileViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            // Get shared ViewModel using home's back stack entry
            val viewModel: HealthProfileViewModel = viewModel(
                viewModelStoreOwner = navController.getBackStackEntry("home")
            )
            HomeScreen(
                onScanClick = { navController.navigate("scan") },
                onProfileClick = { navController.navigate("health_profile") }
            )
        }
        composable("scan") {
            ScanScreen(
                onScanResult = { text ->
                    navController.navigate("result/$text")
                },
                onBackClick = { navController.navigateUp() }
            )
        }
        composable(
            route = "result/{scanResult}"
        ) { backStackEntry ->
            val scanResult = backStackEntry.arguments?.getString("scanResult") ?: ""
            ResultScreen(
                scanResult = scanResult,
                onBackClick = { navController.navigateUp() },
                onHomeClick = { 
                    navController.popBackStack(
                        route = "home",
                        inclusive = false
                    )
                },
                onAnalyzeClick = { text ->
                    navController.navigate("label_analysis/$text")
                }
            )
        }
        composable(
            route = "label_analysis/{text}"
        ) { backStackEntry ->
            val text = backStackEntry.arguments?.getString("text") ?: ""
            LabelAnalysisScreen(
                onBackClick = { navController.navigateUp() },
                text = text
            )
        }
        composable("health_profile") {
            // Get the same shared ViewModel using home's back stack entry
            val viewModel: HealthProfileViewModel = viewModel(
                viewModelStoreOwner = navController.getBackStackEntry("home")
            )
            HealthProfileScreen(
                onBackClick = { navController.navigateUp() }
            )
        }
    }
} 