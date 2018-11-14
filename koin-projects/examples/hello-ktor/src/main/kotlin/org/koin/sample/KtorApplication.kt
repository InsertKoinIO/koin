package org.koin.sample

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.experimental.builder.create
import org.koin.experimental.builder.single
import org.koin.ktor.ext.inject
import org.koin.ktor.ext.installKoin

fun Application.main() {
    // Install Ktor features
    install(DefaultHeaders)
    install(CallLogging)
    installKoin(koinApplication {
        useLogger()
        loadModules(helloAppModule)
    })

    // Lazy inject HelloService
    val service by inject<HelloService>()

    // Routing section
    routing {
        get("/hello") {
            call.respondText(service.sayHello())
        }

        v1()
    }
}

val helloAppModule = module(createdAtStart = true) {
    single<HelloService> { create<HelloServiceImpl>() }
    single<HelloRepository>()
}

fun main(args: Array<String>) {
    // Start Ktor
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}
