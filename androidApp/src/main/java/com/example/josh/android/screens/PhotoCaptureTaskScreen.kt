package com.example.josh.android.screens

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.josh.android.navigation.AppScreen
import com.example.josh.android.recorder.AudioRecorder
import com.example.josh.android.storage.TaskStorageAndroid
import com.example.josh.ui.components.AppHeader
import com.example.josh.ui.components.AudioPlayerCard
import com.example.josh.ui.components.InstructionText
import com.example.josh.ui.components.PrimaryButton
import com.example.josh.ui.components.SecondaryButton
import kotlinx.coroutines.delay
import model.TaskItem
import java.io.File
import java.io.FileOutputStream

@Composable
fun PhotoCaptureTaskScreen(navController: NavHostController) {

    val context = LocalContext.current
    val storage = remember { TaskStorageAndroid(context) }

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var description by remember { mutableStateOf("") }

    var isRecording by remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf(0) }
    var error by remember { mutableStateOf("") }
    var recorder: AudioRecorder? by remember { mutableStateOf(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val photo = result.data?.extras?.get("data") as? Bitmap
            bitmap = photo
        }
    }

    Scaffold(
        topBar = { AppHeader("Recording Tasks", onBack = { navController.popBackStack() }) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {

            InstructionText("Explain what you see in the photo in your language")

            Spacer(Modifier.height(16.dp))

            SecondaryButton("Capture Image") {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraLauncher.launch(intent)
            }

            Spacer(Modifier.height(20.dp))

            bitmap?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Describe the image") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(20.dp))

            SecondaryButton(
                text = if (isRecording) "Stop Recording" else "Record Description",
            ) {
                if (!isRecording) {
                    val f = File(context.filesDir, "photo_desc_${System.currentTimeMillis()}.m4a")
                    recorder = AudioRecorder(f)
                    recorder!!.startRecording()
                    duration = 0
                } else recorder?.stopRecording()

                isRecording = !isRecording
            }

            // Timer logic
            LaunchedEffect(isRecording) {
                if (isRecording) {
                    while (isRecording) {
                        delay(1000)
                        duration++
                    }
                }
            }

            if (duration > 0 && !isRecording) {
                Spacer(Modifier.height(14.dp))
                Text("Submitted Recording", fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
                AudioPlayerCard()
            }

            if (error.isNotEmpty()) {
                Text(error, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(30.dp))

            PrimaryButton(
                text = "Submit",
                enabled = bitmap != null && description.isNotBlank()
            ) {
                val imgFile = File(context.filesDir, "photo_${System.currentTimeMillis()}.jpg")
                FileOutputStream(imgFile).use {
                    bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }

                val task = TaskItem(
                    id = System.currentTimeMillis(),
                    taskType = "photo_capture",
                    imagePath = imgFile.absolutePath,
                    text = description,
                    audioPath = "",
                    durationSec = duration,
                    timestamp = System.currentTimeMillis().toString()
                )
                storage.saveTask(task)
                navController.navigate(AppScreen.TaskSelection.route)
            }
        }
    }
}
