package com.example.josh.android.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
import com.example.josh.ui.components.AppHeader
import com.example.josh.ui.components.AudioPlayerCard
import com.example.josh.ui.components.CheckItem
import com.example.josh.ui.components.InstructionText
import com.example.josh.ui.components.PrimaryButton
import com.example.josh.ui.components.SecondaryButton
import kotlinx.coroutines.delay
import model.TaskItem
import java.io.File

@Composable
fun TextReadingTaskScreen(navController: NavHostController) {

    val context = LocalContext.current
    val storage = remember { TaskStorageAndroid(context) }

    val passage = """
        Read this passage in your own language. 
        Make sure the reading is clear and without background noise.
    """.trimIndent()

    var isRecording by remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf(0) }
    var noNoise by remember { mutableStateOf(false) }
    var noMistakes by remember { mutableStateOf(false) }
    var recorder: AudioRecorder? by remember { mutableStateOf(null) }

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

            SecondaryButton(
                text = if (isRecording) "Stop Recording" else "Start Recording"
            ) {
                if (!isRecording) {
                    val file = File(context.filesDir, "read_${System.currentTimeMillis()}.m4a")
                    recorder = AudioRecorder(file)
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

            Spacer(Modifier.height(24.dp))

            InstructionText("Before submitting, check the following:")

            Spacer(Modifier.height(8.dp))

            CheckItem("Audio has no background noise", noNoise) { noNoise = it }
            CheckItem("Passage is read clearly without mistakes", noMistakes) { noMistakes = it }

            Spacer(Modifier.height(30.dp))

            PrimaryButton(
                text = "Submit",
                enabled = noNoise && noMistakes
            ) {
                val t = TaskItem(
                    id = System.currentTimeMillis(),
                    taskType = "text_reading",
                    text = passage,
                    durationSec = duration,
                    audioPath = "",
                    timestamp = System.currentTimeMillis().toString()
                )
                storage.saveTask(t)
                navController.popBackStack()
            }
        }
    }
}
