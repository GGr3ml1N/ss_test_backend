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
    /**
     * Функция, к-ая объединяет в себе все предыдущие
     */
    fun tasksRoute(route: Route) = with(route) {
        authenticate(configurations = arrayOf("jwt")) {
            route(path = "/tasks") {
                getTasksRoute()
                getTasksByNameRoute()
                getTasksByPriorityRoute()
                postTaskRoute()
            }
        }
    }

    /**
     * Роут, к-ый возвращает список всех задач.
     * При успехе возвращает 200 со списком
     */
    private fun Route.getTasksRoute() {
        get {
            call.respond(HttpStatusCode.OK, taskRepository.allTasks())
        }
    }

    /**
     * Роут, к-ый возвращает задачу, найденную по имени.
     * При успехе возвращает 200 с задачей
     * Если параметр некорректный, выкидывает 400, если имени нет - 404
     */
    private fun Route.getTasksByNameRoute() {
        get(path = "/byName/{$TASK_NAME}") {
            val name = call.parameters[TASK_NAME]
            if (name == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val tasks = taskRepository.taskByName(name)
            if (tasks == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            call.respond(HttpStatusCode.OK, tasks)
        }
    }

    /**
     * Роут, к-ый возвращает список всех задач, отфильтрованных по приоритету.
     * При успехе возвращает 200 со списком
     * Если параметр некорректный, выкидывает 400, если параметра нет - 404
     */
    private fun Route.getTasksByPriorityRoute() {
        get("/byPriority/{$PRIORITY}") {
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
            call.respond(HttpStatusCode.OK, tasks)
        }
    }

    /**
     * Роут, к-ый добавляет задачу.
     * При успехе возвращает 200 с сообщением "Task has been added"
     * Выкидывает ошибку, если приходит некорректный body или происходит ошибка конвертации, возвращает 400
     */
    private fun Route.postTaskRoute() {
        post {
            try {
                val task: Task = call.receive()
                taskRepository.addTask(task)
                call.respond(HttpStatusCode.OK, "Task has been added")
            } catch (ex: IllegalStateException) {
                call.respond(HttpStatusCode.BadRequest, ex.localizedMessage)
            } catch (ex: JsonConvertException) {
                call.respond(HttpStatusCode.BadRequest, ex.localizedMessage)
            }
        }
    }
}
