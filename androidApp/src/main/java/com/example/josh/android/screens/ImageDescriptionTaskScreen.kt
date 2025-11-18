import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import coil.compose.AsyncImage
import com.example.josh.android.navigation.AppScreen
import com.example.josh.android.recorder.AudioRecorder
import com.example.josh.android.storage.TaskStorageAndroid
import com.example.josh.ui.components.AppHeader
import com.example.josh.ui.components.AudioPlayerCard
import com.example.josh.ui.components.InstructionText
import com.example.josh.ui.components.PrimaryButton
import com.example.josh.ui.components.RoundedImageBox
import com.example.josh.ui.components.SecondaryButton
import kotlinx.coroutines.delay
import model.TaskItem
import java.io.File

@Composable
fun ImageDescriptionTaskScreen(navController: NavHostController) {

    val context = LocalContext.current
    val storage = remember { TaskStorageAndroid(context) }
    val imageUrl = "https://cdn.dummyjson.com/product-images/14/2.jpg"

    var isRecording by remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf(0) }
    var error by remember { mutableStateOf("") }
    var recorder: AudioRecorder? by remember { mutableStateOf(null) }

    Scaffold(
        topBar = { AppHeader(title = "Recording Task", onBack = { navController.popBackStack() }) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {

            InstructionText("Explain what you see in the image in your language")

            Spacer(Modifier.height(16.dp))

            RoundedImageBox(url = imageUrl)

            Spacer(Modifier.height(24.dp))

            Text("Press and hold to record", fontSize = 14.sp)

            Spacer(Modifier.height(12.dp))

            SecondaryButton(
                text = if (isRecording) "Stop Recording" else "Start Recording"
            ) {
                if (!isRecording) {
                    val file = File(context.filesDir, "img_desc_${System.currentTimeMillis()}.m4a")
                    recorder = AudioRecorder(file)
                    recorder!!.startRecording()
                    duration = 0
                } else {
                    recorder?.stopRecording()
                }
                isRecording = !isRecording
            }

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
                Spacer(Modifier.height(6.dp))
                AudioPlayerCard()
            }

            if (error.isNotEmpty()) {
                Text(error, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(28.dp))

            PrimaryButton(
                text = "Submit",
                enabled = duration in 10..20
            ) {
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
            }
        }
    }
}
