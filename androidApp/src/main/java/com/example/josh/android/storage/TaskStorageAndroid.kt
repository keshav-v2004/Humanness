package com.example.josh.android.storage

import android.content.Context
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import model.TaskItem
import kotlin.collections.toMutableList

class TaskStorageAndroid(context: Context) {

    private val prefs = context.getSharedPreferences("tasks", Context.MODE_PRIVATE)

    fun saveTask(t: TaskItem) {
        val list = getTasks().toMutableList()
        list.add(t)
        prefs.edit().putString("list", Json.encodeToString(list)).apply()
    }

    fun getTasks(): List<TaskItem> {
        val raw = prefs.getString("list", "") ?: return emptyList()
        return if (raw.isBlank()) emptyList() else Json.decodeFromString(raw)
    }
}
