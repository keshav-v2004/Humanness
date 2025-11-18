package com.example.josh.android.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.josh.android.recorder.AudioRecorder
import com.example.josh.android.storage.TaskStorageAndroid
import com.example.josh.android.ui.components.AppHeader
import com.example.josh.android.ui.components.AudioPlayerCard
import com.example.josh.android.ui.components.CheckItem
import com.example.josh.android.ui.components.InstructionText
import com.example.josh.android.ui.components.PrimaryButton
import com.example.josh.android.ui.components.PressHoldRecordButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import model.TaskItem
import java.io.File

@Composable
fun TextReadingTaskScreen(navController: NavHostController) {

    val context = LocalContext.current
    val storage = remember { TaskStorageAndroid(context) }

    var passage by remember { mutableStateOf("Loading passage...") }

    var isRecording by remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf(0) }
    var noNoise by remember { mutableStateOf(false) }
    var noMistakes by remember { mutableStateOf(false) }
    var noHindiMistake by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var recorder: AudioRecorder? by remember { mutableStateOf(null) }
    var recordedFile: File? by remember { mutableStateOf(null) }

    // Fetch passage from API (dummyjson products -> description of first product)
    LaunchedEffect(Unit) {
        try {
            val json = withContext(Dispatchers.IO) { URL("https://dummyjson.com/products").readText() }
            val obj = JSONObject(json)
            val arr = obj.getJSONArray("products")
            if (arr.length() > 0) {
                val first = arr.getJSONObject(0)
                passage = first.getString("description")
            } else passage = "No passage available"
        } catch (e: Exception) {
            passage = "Failed to load passage. Please retry later."
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

            InstructionText("Read the following passage:")

            Spacer(Modifier.height(14.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = passage,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(20.dp))

            PressHoldRecordButton(
                isRecording = isRecording,
                onStart = {
                    error = ""
                    recordedFile = File(context.filesDir, "read_${System.currentTimeMillis()}.m4a")
                    recorder = AudioRecorder(recordedFile!!)
                    recorder!!.startRecording()
                    duration = 0
                    isRecording = true
                },
                onStop = {
                    recorder?.stopRecording()
                    isRecording = false
                    if (duration < 10) error = "Recording too short (min 10 s)."
                    else if (duration > 20) error = "Recording too long (max 20 s)."
                }
            )

            // Timer logic
            LaunchedEffect(isRecording) {
                if (isRecording) {
                    while (isRecording) {
                        delay(1000)
                        duration++
                    }
                }
            }

            if (duration > 0 && !isRecording && error.isEmpty()) {
                Spacer(Modifier.height(14.dp))
                Text("Submitted Recording", fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
                AudioPlayerCard()
            }

            if (error.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(error, fontSize = 13.sp, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(24.dp))

            InstructionText("Before submitting, check the following:")

            Spacer(Modifier.height(8.dp))

            CheckItem("No background noise", noNoise) { noNoise = it }
            CheckItem("No mistakes while reading", noMistakes) { noMistakes = it }
            CheckItem("Beech me koi galti nahi hai", noHindiMistake) { noHindiMistake = it }

            Spacer(Modifier.height(30.dp))

            PrimaryButton(
                text = "Submit",
                enabled = error.isEmpty() && duration in 10..20 && noNoise && noMistakes && noHindiMistake && recordedFile != null
            ) {
                val t = TaskItem(
                    id = System.currentTimeMillis(),
                    taskType = "text_reading",
                    text = passage,
                    durationSec = duration,
                    audioPath = recordedFile?.absolutePath ?: "",
                    timestamp = System.currentTimeMillis().toString()
                )
                storage.saveTask(t)
                navController.popBackStack()
            }
        }
    }
}
