package com.ggr3ml1n.presentation.main

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

const val AUDIENCE = "audience"
const val DOMAIN = "domain"
const val REALM = "realm"
const val SECRET = "secret"

fun Application.configureAuth() {
    val jwtAudience: String = environment.getPropertyFromConfigJWT(AUDIENCE)
    val jwtDomain: String = environment.getPropertyFromConfigJWT(DOMAIN)
    val jwtRealm: String = environment.getPropertyFromConfigJWT(REALM)
    val jwtSecret: String = environment.getPropertyFromConfigJWT(SECRET)
    authentication {
        jwt(name = "jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { jwtCredential ->
                if (jwtCredential.payload.audience.contains(jwtAudience)) JWTPrincipal(jwtCredential.payload) else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
}

fun ApplicationEnvironment.getPropertyFromConfigJWT(propertyName: String): String =
    config.config("jwt").property(propertyName).getString()
