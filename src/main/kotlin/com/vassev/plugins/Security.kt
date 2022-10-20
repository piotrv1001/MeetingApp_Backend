package com.vassev.plugins

import io.ktor.auth.*
import io.ktor.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.vassev.chat_room.ChatSession
import com.vassev.data.requests.WebSocketRequest
import com.vassev.security.token.TokenConfig
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.sessions.*
import io.ktor.util.*

fun Application.configureSecurity(tokenConfig: TokenConfig) {
    authentication {
        jwt {
            realm = this@configureSecurity.environment.config.property("jwt.realm").getString()
            verifier(
                JWT
                    .require(Algorithm.HMAC256(tokenConfig.secret))
                    .withAudience(tokenConfig.audience)
                    .withIssuer(tokenConfig.issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(tokenConfig.audience)) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
    install(Sessions) {
        cookie<ChatSession>("SESSION")
    }

    intercept(ApplicationCallPipeline.Features) {
        if(call.sessions.get<ChatSession>() == null) {
            val userId = call.request.queryParameters["userId"] ?: ""
            val meetingId = call.request.queryParameters["meetingId"] ?: ""
            val username = call.request.queryParameters["username"] ?: ""
            call.sessions.set(ChatSession(
                sessionId = generateNonce(),
                userId = userId,
                meetingId = meetingId,
                username = username
            ))
        }
    }
}
