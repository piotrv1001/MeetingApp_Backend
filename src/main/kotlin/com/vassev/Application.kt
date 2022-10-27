package com.vassev

import com.vassev.di.mainModule
import io.ktor.application.*
import com.vassev.plugins.*
import com.vassev.security.token.TokenConfig
import org.koin.ktor.ext.Koin

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    install(Koin) {
        modules(mainModule)
    }
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 24L * 1000L * 60L * 60L,
        secret = "JDHMnJhgq2C0TDFiYxwB"
//        secret = System.getenv("JWT_SECRET")
    )
    configureSockets()
    configureSecurity(tokenConfig)
    configureSerialization()
    configureMonitoring()
    configureRouting(tokenConfig)
}
