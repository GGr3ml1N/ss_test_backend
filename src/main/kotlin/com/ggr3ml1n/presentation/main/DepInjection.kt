package com.ggr3ml1n.presentation.main

import com.ggr3ml1n.data.db.FakeDb
import com.ggr3ml1n.domain.repositories.AuthRepository
import com.ggr3ml1n.domain.repositories.TaskRepository
import com.ggr3ml1n.presentation.routes.AuthRoute
import com.ggr3ml1n.presentation.routes.TaskRoute
import io.ktor.server.application.*
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

private val dataModule: Module = module {
    single { FakeDb() }
}

private val domainModule: Module = module {
    single { AuthRepository(get()) }
    single { TaskRepository(get()) }
}

private val routesModule: Module = module {
    single<TaskRoute> { TaskRoute(get()) }
    single<AuthRoute> { AuthRoute(get()) }
}

/**
 * Внедрение зависимостей
 */
fun Application.configureDependencies() {
    install(Koin) {
        slf4jLogger()
        modules(
            dataModule,
            domainModule,
            routesModule,
        )
    }
}

