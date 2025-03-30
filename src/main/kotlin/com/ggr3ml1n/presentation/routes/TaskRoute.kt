package com.ggr3ml1n.presentation.routes

import com.ggr3ml1n.data.models.Task
import com.ggr3ml1n.domain.repositories.TaskRepository
import com.ggr3ml1n.data.models.Priority
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private const val TASK_NAME = "taskName"
private const val PRIORITY = "priority"

class TaskRoute(private val taskRepository: TaskRepository) {
    fun tasksRoute(route: Route) = with(route){
        authenticate(configurations = arrayOf("jwt")) {
            route(path = "/tasks") {
                getTasksRoute()
                getTasksByNameRoute()
                getTasksByPriorityRoute()
                postTaskRoute()
            }
        }
    }

    private fun Route.getTasksRoute() {
        get {
            call.respond(HttpStatusCode.OK, taskRepository.allTasks())
        }
    }

    private fun Route.getTasksByNameRoute() {
        get(path = "/byName/{$TASK_NAME}") {
            val name = call.parameters[TASK_NAME]
            if (name == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val task = taskRepository.taskByName(name)
            if (task == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            call.respond(task)
        }
    }

    private fun Route.getTasksByPriorityRoute() {
        get("/byPriority/{$PRIORITY}") {
            try {
                val priorityAsText = call.parameters[PRIORITY]
                if (priorityAsText == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val priority = Priority.valueOf(priorityAsText)
                val tasks = taskRepository.tasksByPriority(priority)
                if (tasks.isEmpty()) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respond(tasks)
            } catch (ex: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }

    private fun Route.postTaskRoute() {
        post {
            try {
                val task: Task = call.receive()
                taskRepository.addTask(task)
            } catch (ex: IllegalStateException) {
                call.respond(HttpStatusCode.BadRequest, ex.localizedMessage)
            } catch (ex: JsonConvertException) {
                call.respond(HttpStatusCode.BadRequest, ex.localizedMessage)
            }
        }
    }
}
