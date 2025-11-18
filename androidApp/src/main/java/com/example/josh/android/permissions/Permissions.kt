package com.example.josh.android.permissions

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequirePermissions(
    permissions: List<String>,
    rationale: String,
    content: @Composable () -> Unit
) {
    val state = rememberMultiplePermissionsState(permissions = permissions)
    val permanentlyDenied = state.permissions.any { perm ->
        when (val s = perm.status) {
            is PermissionStatus.Denied -> !s.shouldShowRationale
            PermissionStatus.Granted -> false
        }
    }

    when {
        state.allPermissionsGranted -> content()
        permanentlyDenied -> PermanentlyDeniedCard(rationale, state)
        else -> RequestPermissionsCard(rationale, state)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun RequestPermissionsCard(rationale: String, state: MultiplePermissionsState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("Permissions Required", fontWeight = FontWeight.Bold)
            }
            Text(rationale, style = MaterialTheme.typography.bodyMedium)
            Text(buildPermissionStatusSummary(state), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(onClick = { state.launchMultiplePermissionRequest() }) { Text("Grant Permissions") }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermanentlyDeniedCard(rationale: String, state: MultiplePermissionsState) {
    val context = LocalContext.current
    val intent = remember {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Spacer(Modifier.width(8.dp))
                Text("Permissions Needed in Settings", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
            }
            Text(rationale, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onErrorContainer)
            Text(buildPermissionStatusSummary(state), style = MaterialTheme.typography.bodySmall)
            OutlinedButton(onClick = { context.startActivity(intent) }) { Text("Open App Settings") }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
private fun buildPermissionStatusSummary(state: MultiplePermissionsState): String {
    return state.permissions.joinToString("\n") { perm ->
        val status = when (perm.status) {
            PermissionStatus.Granted -> "Granted"
            is PermissionStatus.Denied -> if ((perm.status as PermissionStatus.Denied).shouldShowRationale) "Needs Rationale" else "Permanently Denied"
        }
        "${perm.permission}: ${status}"
    }
}

