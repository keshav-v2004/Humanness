package com.example.josh.android.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.josh.android.navigation.AppScreen
import com.example.josh.ui.components.AppHeader
import com.example.josh.ui.components.TaskOptionCard

@Composable
fun TaskSelectionScreen(navController: NavHostController) {

    Scaffold(
        topBar = { AppHeader("Choose a Task") }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            TaskOptionCard(
                title = "Text Reading Task",
                description = "Read aloud a given passage",
                icon = Icons.Default.Email,
                onClick = { navController.navigate(AppScreen.TextReading.route) }
            )

            TaskOptionCard(
                title = "Image Description Task",
                description = "Describe what you see in an image",
                icon = Icons.Default.Face,
                onClick = { navController.navigate(AppScreen.ImageDescription.route) }
            )

            TaskOptionCard(
                title = "Photo Capture Task",
                description = "Capture a photo and describe it",
                icon = Icons.Default.PlayArrow,
                onClick = { navController.navigate(AppScreen.PhotoCapture.route) }
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
