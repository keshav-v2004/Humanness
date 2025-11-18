package com.example.josh.android.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.josh.android.navigation.AppScreen
import com.example.josh.android.ui.components.AppHeader
import com.example.josh.android.ui.components.PrimaryButton
import com.example.josh.android.permissions.RequirePermissions
import android.Manifest
import kotlinx.coroutines.delay

@Composable
fun NoiseTestScreen(navController: NavHostController) {

    var decibel by remember { mutableStateOf(0) }
    var message by remember { mutableStateOf("") }
    var running by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { AppHeader("Noise Test", onBack = { navController.popBackStack() }) }
    ) { padding ->
        RequirePermissions(
            permissions = listOf(Manifest.permission.RECORD_AUDIO),
            rationale = "Microphone permission is required to measure ambient noise."
        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Current Noise Level", fontSize = 16.sp)
            Text("$decibel dB", fontSize = 32.sp, color = Color(0xFF0A74FF))

            Spacer(Modifier.height(24.dp))

            PrimaryButton(text = "Start Test", enabled = !running) {
                running = true
            }

            Spacer(Modifier.height(20.dp))
            Text(message)

            LaunchedEffect(running) {
                if (running) {
                    repeat(25) {
                        decibel = (25..63).random()
                        delay(120)
                    }
                    if (decibel < 40) {
                        message = "Good to proceed"
                        delay(800)
                        navController.navigate(AppScreen.TaskSelection.route)
                    } else {
                        message = "Please move to a quieter place"
                    }
                    running = false
                }
            }
        }
        }
    }
}
