package com.ggr3ml1n.presentation.main

import io.ktor.server.application.*
import io.ktor.server.netty.*

/**
 * Понятия не имею как сделать по красоте, чтоб как будто на прод, сделал как надумал.
 */
fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureDependencies()
    configureSerialization()
    configureAuth()
    configureRouting()
}
