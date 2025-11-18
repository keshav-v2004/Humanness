package com.example.josh.android.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.josh.android.navigation.AppScreen

@Composable
fun TaskSelectionScreen(navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { navController.navigate(AppScreen.TextReading.route) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Text Reading Task") }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate(AppScreen.ImageDescription.route) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Image Description Task") }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate(AppScreen.PhotoCapture.route) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Photo Capture Task") }
    }
}
