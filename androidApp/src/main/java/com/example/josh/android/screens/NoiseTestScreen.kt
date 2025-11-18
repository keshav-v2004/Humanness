package com.example.josh.android.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.josh.android.navigation.AppScreen
import kotlinx.coroutines.delay

@Composable
fun NoiseTestScreen(navController: NavHostController) {

    var decibel by remember { mutableStateOf(0) }
    var status by remember { mutableStateOf("") }
    var testRunning by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Noise Test", fontSize = 24.sp)

        Spacer(Modifier.height(20.dp))

        Text("Current Noise Level: $decibel dB", fontSize = 20.sp)

        Spacer(Modifier.height(30.dp))

        Button(
            onClick = {
                testRunning = true
            },
            enabled = !testRunning
        ) {
            Text("Start Test")
        }

        Spacer(Modifier.height(20.dp))

        Text(status, fontSize = 18.sp)

        LaunchedEffect(testRunning) {
            if (testRunning) {
                repeat(20) {
                    decibel = (20..60).random()
                    delay(150)
                }

                if (decibel < 40) {
                    status = "Good to proceed"
                    delay(1000)
                    navController.navigate(AppScreen.TaskSelection.route)
                } else {
                    status = "Please move to a quieter place"
                }

                testRunning = false
            }
        }
    }
}
