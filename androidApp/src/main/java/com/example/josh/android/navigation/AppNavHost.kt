package com.example.josh.android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.josh.android.screens.ImageDescriptionTaskScreen
import com.example.josh.android.screens.NoiseTestScreen
import com.example.josh.android.screens.PhotoCaptureTaskScreen
import com.example.josh.android.screens.StartScreen
import com.example.josh.android.screens.TaskHistoryScreen
import com.example.josh.android.screens.TaskSelectionScreen
import com.example.josh.android.screens.TextReadingTaskScreen


sealed class AppScreen(val route: String) {
    object Start : AppScreen("start")
    object NoiseTest : AppScreen("noiseTest")
    object TaskSelection : AppScreen("taskSelection")
    object TextReading : AppScreen("textReading")
    object ImageDescription : AppScreen("imageDescription")
    object PhotoCapture : AppScreen("photoCapture")
    object TaskHistory : AppScreen("taskHistory")
}

@Composable
fun AppNavHost(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = AppScreen.Start.route
    ) {

        composable(AppScreen.Start.route) {
            StartScreen(navController)
        }
        composable(AppScreen.NoiseTest.route) {
            NoiseTestScreen(navController)
        }
        composable(AppScreen.TaskSelection.route) {
            TaskSelectionScreen(navController)
        }
        composable(AppScreen.TextReading.route) {
            TextReadingTaskScreen(navController)
        }
        composable(AppScreen.ImageDescription.route) {
            ImageDescriptionTaskScreen(navController)
        }
        composable(AppScreen.PhotoCapture.route) {
            PhotoCaptureTaskScreen(navController)
        }
        composable(AppScreen.TaskHistory.route) {
            TaskHistoryScreen(navController)
        }
    }
}
