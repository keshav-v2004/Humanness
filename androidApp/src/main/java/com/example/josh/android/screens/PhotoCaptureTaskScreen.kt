package com.example.josh.android.screens

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.josh.android.navigation.AppScreen
import com.example.josh.android.recorder.AudioRecorder
import com.example.josh.android.storage.TaskStorageAndroid
import kotlinx.coroutines.delay
import model.TaskItem
import java.io.File
import java.io.FileOutputStream

@Composable
fun PhotoCaptureTaskScreen(navController: NavHostController) {

    val context = LocalContext.current
    val storage = remember { TaskStorageAndroid(context) }

    var photoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var description by remember { mutableStateOf("") }

    var isRecording by remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf(0) }
    var error by remember { mutableStateOf("") }

    var recorder: AudioRecorder? by remember { mutableStateOf(null) }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val photo = result.data?.extras?.get("data") as Bitmap
            photoBitmap = photo
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp)
    ) {
        Button(
            onClick = {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraLauncher.launch(intent)
            }
        ) {
            Text("Capture Image")
        }

        Spacer(Modifier.height(20.dp))

        photoBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Describe the photo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                if (!isRecording) {
                    val file = File(context.filesDir, "photo_desc_${System.currentTimeMillis()}.m4a")
                    recorder = AudioRecorder(file)
                    recorder!!.startRecording()
                    duration = 0
                } else {
                    recorder?.stopRecording()

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
            Text(if (isRecording) "Stop Recording" else "Record Audio (Optional)")
        }

        if (error.isNotEmpty())
            Text(error, color = MaterialTheme.colorScheme.error)

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

                if (photoBitmap == null) return@Button

                // Save image locally
                val imgFile = File(context.filesDir, "photo_${System.currentTimeMillis()}.jpg")
                FileOutputStream(imgFile).use {
                    photoBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }

                val task = TaskItem(
                    id = System.currentTimeMillis(),
                    taskType = "photo_capture",
                    imagePath = imgFile.absolutePath,
                    audioPath = "", // or actual path if recorded
                    text = description,
                    durationSec = duration,
                    timestamp = System.currentTimeMillis().toString()
                )

                storage.saveTask(task)
                navController.navigate(AppScreen.TaskSelection.route)
            },
            enabled = photoBitmap != null && description.isNotBlank()
        ) {
            Text("Submit")
        }
    }
}
