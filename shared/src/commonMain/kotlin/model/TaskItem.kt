package model

import kotlinx.serialization.Serializable

@Serializable
data class TaskItem(
    val id: Long,
    val taskType: String,
    val text: String? = null,
    val imageUrl: String? = null,
    val imagePath: String? = null,
    val audioPath: String? = null,
    val durationSec: Int,
    val timestamp: String
)
