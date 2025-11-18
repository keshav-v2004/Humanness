package com.example.josh.android.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.josh.android.storage.TaskStorageAndroid
import android.graphics.BitmapFactory
import model.TaskItem

@Composable
fun TaskHistoryScreen(navController: NavHostController) {

    val context = LocalContext.current
    val storage = remember { TaskStorageAndroid(context) }

    val tasks = storage.getTasks()
    val totalDuration = tasks.sumOf { it.durationSec }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total Tasks: ${tasks.size}")
            Text("Total Duration: ${totalDuration}s")
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(tasks) { task ->
                TaskItemPreview(task)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun TaskItemPreview(task: TaskItem) {

    Card(
        Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {

        Column(Modifier.padding(12.dp)) {

            Text("Task ID: ${task.id}")
            Text("Type: ${task.taskType}")
            Text("Duration: ${task.durationSec}s")
            Text("Timestamp: ${task.timestamp}")

            Spacer(Modifier.height(8.dp))

            // Show text or image based on task
            task.text?.let { Text(it.take(60)) }

            if (task.imageUrl != null) {
                Image(
                    painter = rememberImagePainter(task.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
            }

            if (task.imagePath != null) {
                val bitmap = BitmapFactory.decodeFile(task.imagePath)
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    )
                }
            }
        }
    }
}
