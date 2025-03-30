package com.ggr3ml1n.presentation.main

import com.ggr3ml1n.presentation.routes.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Application.configureRouting() {
    routing {
        dependency<TaskRoute>().tasksRoute(this)
        dependency<AuthRoute>().authRoutes(this)
    }
}

private inline fun <reified T : Any> Application.dependency() = get<T>()


