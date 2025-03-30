package com.ggr3ml1n.domain.repositories

import com.ggr3ml1n.data.db.FakeDb
import com.ggr3ml1n.data.models.Priority
import com.ggr3ml1n.data.models.Task

class TaskRepository(private val fakeDb: FakeDb) {
    fun allTasks(): List<Task> = fakeDb.tasks

    fun tasksByPriority(priority: Priority) = fakeDb.tasks.filter {
        it.priority == priority
    }

    fun taskByName(name: String) = fakeDb.tasks.find {
        it.name.equals(name, ignoreCase = true)
    }

    fun addTask(task: Task) {
        if (taskByName(task.name) != null) {
            throw IllegalStateException("Cannot duplicate task names!")
        }
        fakeDb.tasks.add(task)
    }
}