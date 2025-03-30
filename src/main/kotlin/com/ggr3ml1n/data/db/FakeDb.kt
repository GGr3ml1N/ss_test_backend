package com.ggr3ml1n.data.db

import com.ggr3ml1n.data.models.Task
import com.ggr3ml1n.data.models.Priority
import com.ggr3ml1n.data.models.User

class FakeDb {
    val tasks: MutableList<Task> = mutableListOf(
        Task("cleaning", "Clean the house", Priority.Low),
        Task("gardening", "Mow the lawn", Priority.Medium),
        Task("shopping", "Buy the groceries", Priority.High),
        Task("painting", "Paint the fence", Priority.Medium)
    )

    val users: MutableList<User> = mutableListOf()
}