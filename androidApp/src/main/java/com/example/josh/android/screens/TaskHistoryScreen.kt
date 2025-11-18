package com.example.josh.android.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.josh.android.storage.TaskStorageAndroid
import com.example.josh.ui.components.AppHeader
import com.example.josh.ui.components.RoundedImageBox
import model.TaskItem
import java.util.Date


@Composable
fun TaskHistoryScreen(navController: NavHostController) {

    val context = LocalContext.current
    val storage = remember { TaskStorageAndroid(context) }
    val tasks = storage.getTasks()

    val totalDuration = tasks.sumOf { it.durationSec }

    Scaffold(
        topBar = { AppHeader("Task History", onBack = { navController.popBackStack() }) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatCard("Total Tasks", tasks.size.toString())
                StatCard("Duration Recorded", formatDuration(totalDuration))
            }

            Spacer(Modifier.height(20.dp))

            Text("Tasks", fontSize = 18.sp)

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tasks) { task ->
                    TaskHistoryCard(task)
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .height(90.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, fontSize = 14.sp, color = Color.Gray)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TaskHistoryCard(task: TaskItem) {

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {

            Text("Task - ${task.id}", fontSize = 16.sp, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(4.dp))

            Text("Duration: ${task.durationSec}s", fontSize = 14.sp, color = Color.Gray)

            val date = Date(task.timestamp.toLong()).toString().substring(0, 16)
            Text(date, fontSize = 13.sp, color = Color.Gray)

            Spacer(Modifier.height(12.dp))

            if (task.text != null) {
                Text(task.text!!.take(60), fontSize = 14.sp)
            }

            if (task.imageUrl != null) {
                RoundedImageBox(task.imageUrl!!)
            }

            if (task.imagePath != null) {
                val bitmap = BitmapFactory.decodeFile(task.imagePath)
                bitmap?.let {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

fun formatDuration(sec: Int): String {
    val min = sec / 60
    val s = sec % 60
    return "${min}m ${s}s"
}
