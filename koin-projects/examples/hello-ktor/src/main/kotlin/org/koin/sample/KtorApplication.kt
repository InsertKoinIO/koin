package org.koin.sample

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.Logger.slf4jLogger
import org.koin.dsl.module
import org.koin.experimental.builder.single
import org.koin.experimental.builder.singleBy
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject

fun Application.main() {
    // Install Ktor features
    install(DefaultHeaders)
    install(CallLogging)
    install(Koin) {
        slf4jLogger()
        modules(helloAppModule)
    }
    val helloService by inject<HelloService>()
    // Routing section
    routing {
        get("/hello") {
            call.respondText(helloService.sayHello())
        }
        declareRoutes()
    }
}

private fun Routing.declareRoutes() {
    v1()
    bye()
}

val helloAppModule = module(createdAtStart = true) {
    singleBy<HelloService, HelloServiceImpl>()
    single<HelloRepository>()
}

fun main(args: Array<String>) {
    // Start Ktor
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}
