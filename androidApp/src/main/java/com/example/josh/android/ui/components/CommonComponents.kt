package com.example.josh.android.ui.components

import android.R.attr.description
import android.R.attr.onClick
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import coil.compose.AsyncImagePainter
import kotlinx.coroutines.Dispatchers
import java.net.URL
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeader(title: String, onBack: (() -> Unit)? = null) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, null)
                }
            }
        }
    )
}

@Composable
fun InstructionText(text: String) {
    Text(
        text = text,
        fontSize = 15.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun RoundedImageBox(url: String) {
    val context = LocalContext.current
    var reloadKey by remember { mutableStateOf(0) }
    var fallbackBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(url)
                .setHeader("Accept", "image/jpeg,image/png,image/webp;q=0.9,*/*;q=0.8")
                .crossfade(true)
                .allowHardware(false)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        ) {
            when (painter.state) {
                is AsyncImagePainter.State.Loading -> Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE9EEF4)),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(strokeWidth = 2.dp) }
                is AsyncImagePainter.State.Error -> {
                    LaunchedEffect(reloadKey) {
                        try {
                            fallbackBitmap = BitmapFactory.decodeStream(URL(url).openStream())
                        } catch (_: Exception) { fallbackBitmap = null }
                    }
                    if (fallbackBitmap != null) {
                        Image(
                            bitmap = fallbackBitmap!!.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color(0xFFFFE5E5))
                            .clickable { reloadKey++ },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Image error", color = Color.Red, fontSize = 12.sp)
                            Spacer(Modifier.height(6.dp))
                            Text("Tap to retry", fontSize = 11.sp, color = Color.DarkGray)
                        }
                    }
                }
                else -> SubcomposeAsyncImageContent()
            }
        }
    }
}


@Composable
fun PressHoldRecordButton(
    isRecording: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
    labelIdle: String = "Press & Hold to Record",
    labelRecording: String = "Recording..."
) {
    var pressed by remember { mutableStateOf(false) }
    // Use raw pointer handling to guarantee press-down start and release stop independent of parent state timing.
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        pressed = true
                        onStart()
                        // Wait for all pointers up or cancellation
                        var released = false
                        while (!released) {
                            val event = awaitPointerEvent()
                            val allUp = event.changes.all { !it.pressed }
                            if (allUp) {
                                released = true
                                pressed = false
                                onStop()
                            }
                        }
                    }
                }
            },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (pressed || isRecording) Color(0xFF0A74FF) else Color(0xFFE9EEF4))
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = if (pressed || isRecording) labelRecording else labelIdle, color = if (pressed || isRecording) Color.White else Color.DarkGray)
        }
    }
}

@Composable
fun PrimaryButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text)
    }
}

@Composable
fun SecondaryButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text)
    }
}

@Composable
fun CheckItem(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Checkbox(checked = checked, onCheckedChange = onChange)
        Text(label, fontSize = 15.sp)
    }
}

@Composable
fun AudioPlayerCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFE9EEF4)),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(modifier = Modifier.padding(horizontal = 20.dp)) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color(0xFF0A74FF)
            )
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(4.dp)
                    .background(Color.Gray)
            )
        }
    }
}

@Composable
fun TaskOptionCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F8FF))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(Modifier.width(16.dp))

            Column {
                Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text(
                    description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}




