package storage

import model.TaskItem

class TaskStorage {
    private val items = mutableListOf<TaskItem>()

    fun saveTask(task: TaskItem) {
        items.add(task)
    }

    fun getTasks(): List<TaskItem> = items.toList()
}
