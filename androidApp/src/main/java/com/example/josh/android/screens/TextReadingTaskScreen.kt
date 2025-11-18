package com.example.josh.android.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.josh.android.navigation.AppScreen
import com.example.josh.android.recorder.AudioRecorder
import com.example.josh.android.storage.TaskStorageAndroid

import model.TaskItem
import java.io.File

@Composable
fun TextReadingTaskScreen(navController: NavHostController) {

    val context = LocalContext.current
    val storage = remember { TaskStorageAndroid(context) }
    val text = "Read this passage aloud in your language."

    var isRecording by remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf(0) }
    var error by remember { mutableStateOf("") }

    var noNoise by remember { mutableStateOf(false) }
    var noMistakes by remember { mutableStateOf(false) }

    var recorder: AudioRecorder? by remember { mutableStateOf(null) }

    Column(modifier = Modifier.padding(24.dp)) {

        Text(text)

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                if (!isRecording) {
                    val file = File(context.filesDir, "text_reading_${System.currentTimeMillis()}.m4a")
                    recorder = AudioRecorder(file)
                    recorder!!.startRecording()
                } else {
                    recorder?.stopRecording()
                }
                isRecording = !isRecording
            }
        ) {
            Text(if (isRecording) "Stop Recording" else "Press & Hold to Record")
        }

        if (error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(20.dp))

        Row {
            Checkbox(checked = noNoise, onCheckedChange = { noNoise = it })
            Text("No background noise")
        }

        Row {
            Checkbox(checked = noMistakes, onCheckedChange = { noMistakes = it })
            Text("No mistakes while reading")
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                val file = File(context.filesDir, "text_reading")
                val task = TaskItem(
                    id = System.currentTimeMillis(),
                    taskType = "text_reading",
                    text = text,
                    audioPath = file.absolutePath,
                    durationSec = duration,
                    timestamp = System.currentTimeMillis().toString()
                )
                storage.saveTask(task)
                navController.navigate(AppScreen.TaskSelection.route)
            },
            enabled = noNoise && noMistakes
        ) {
            Text("Submit")
        }
    }
}
