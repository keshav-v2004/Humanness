package com.example.josh.android.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.josh.android.navigation.AppScreen
import com.example.josh.android.recorder.AudioRecorder
import com.example.josh.android.storage.TaskStorageAndroid
import java.io.File
import kotlinx.coroutines.delay
import model.TaskItem

@Composable
fun ImageDescriptionTaskScreen(navController: NavHostController) {

    val context = LocalContext.current
    val storage = remember { TaskStorageAndroid(context) }

    val imageUrl = "https://cdn.dummyjson.com/product-images/14/2.jpg"

    var isRecording by remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf(0) }
    var error by remember { mutableStateOf("") }

    var recorder: AudioRecorder? by remember { mutableStateOf(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text("Describe what you see in your language:")
        Spacer(Modifier.height(16.dp))

        Image(
            painter = rememberImagePainter(imageUrl),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        )

        Spacer(Modifier.height(20.dp))

        // Press & Hold Recording Simulation (toggle button)
        Button(
            onClick = {
                if (!isRecording) {
                    val file = File(context.filesDir, "img_desc_${System.currentTimeMillis()}.m4a")
                    recorder = AudioRecorder(file)
                    recorder!!.startRecording()
                    duration = 0
                } else {
                    recorder?.stopRecording()

                    // Validation
                    if (duration < 10) {
                        error = "Recording too short (min 10 seconds)"
                        return@Button
                    }
                    if (duration > 20) {
                        error = "Recording too long (max 20 seconds)"
                        return@Button
                    }
                }
                isRecording = !isRecording
            }
        ) {
            Text(if (isRecording) "Stop Recording" else "Press & Hold to Record")
        }

        if (error.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Text(error, color = MaterialTheme.colorScheme.error)
        }

        // Timer
        LaunchedEffect(isRecording) {
            if (isRecording) {
                while (isRecording) {
                    delay(1000)
                    duration++
                }
            }
        }

        Spacer(Modifier.height(30.dp))

        Button(
            onClick = {
                val file = File(context.filesDir, "img_desc")
                val task = TaskItem(
                    id = System.currentTimeMillis(),
                    taskType = "image_description",
                    imageUrl = imageUrl,
                    audioPath = file.absolutePath,
                    durationSec = duration,
                    timestamp = System.currentTimeMillis().toString()
                )
                storage.saveTask(task)
                navController.navigate(AppScreen.TaskSelection.route)
            },
            enabled = duration in 10..20
        ) {
            Text("Submit")
        }
    }
}
