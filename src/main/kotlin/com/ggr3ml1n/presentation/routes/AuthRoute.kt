package com.ggr3ml1n.presentation.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ggr3ml1n.data.models.User
import com.ggr3ml1n.domain.models.Response
import com.ggr3ml1n.domain.repositories.AuthRepository
import com.ggr3ml1n.presentation.main.AUDIENCE
import com.ggr3ml1n.presentation.main.DOMAIN
import com.ggr3ml1n.presentation.main.SECRET
import com.ggr3ml1n.presentation.main.getPropertyFromConfigJWT
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import kotlin.time.Duration.Companion.hours

class AuthRoute(private val authRepository: AuthRepository) {
    fun authRoutes(route: Route): Unit = with(route) {
        postLoginUserRoute()
        postRegisterUserRoute()
        getAllUsersRoute()
    }

    private fun Route.postRegisterUserRoute() {
        post("/register") {
            try {
                val user: User = call.receive()

                if (authRepository.compareUser(user)) {
                    call.respond(HttpStatusCode.Conflict, "User already exists")
                    return@post
                }

                authRepository.addUser(user)
                call.respond(HttpStatusCode.OK, "User has been registered")
            } catch (ex: IllegalStateException) {
                call.respond(HttpStatusCode.BadRequest, ex.localizedMessage)
            } catch (ex: JsonConvertException) {
                call.respond(HttpStatusCode.BadRequest, ex.localizedMessage)
            }
        }
    }

    private fun Route.postLoginUserRoute() {
        val jwtAudience: String = environment.getPropertyFromConfigJWT(AUDIENCE)
        val jwtDomain: String = environment.getPropertyFromConfigJWT(DOMAIN)
        val jwtSecret: String = environment.getPropertyFromConfigJWT(SECRET)
        val expiresAt = Clock.System.now().plus(24.hours).toJavaInstant()
        post("/login") {
            try {
                val user: User = call.receive()
                if (authRepository.isUserExist(user)) {
                    val token = JWT.create()
                        .withIssuer(jwtDomain)
                        .withAudience(jwtAudience)
                        .withClaim("user.login", user.login)
                        .withExpiresAt(expiresAt)
                        .sign(Algorithm.HMAC256(jwtSecret))
                    call.respond(status = HttpStatusCode.OK, message = Response(token = token))
                } else
                    call.respond(status = HttpStatusCode.BadRequest, message = "User doesn't exist")
            } catch (ex: IllegalStateException) {
                call.respond(HttpStatusCode.BadRequest, ex.localizedMessage)
            } catch (ex: JsonConvertException) {
                call.respond(HttpStatusCode.BadRequest, ex.localizedMessage)
            }

        }
    }

    @Suppress("Test route")
    private fun Route.getAllUsersRoute() {
        get("/users") {
            call.respond(authRepository.users)
        }
    }
}
