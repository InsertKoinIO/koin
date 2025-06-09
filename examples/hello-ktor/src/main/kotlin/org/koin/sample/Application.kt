package org.koin.sample

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.logger.Level
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.*

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080) {
        mainModule()
        module2()
    }.start(wait = true)
}

fun Application.mainModule() {
    install(CallLogging)
    install(Koin) {
        printLogger(Level.DEBUG)
        modules(appModule)
    }

    // Install Ktor features
    monitor.subscribe(KoinApplicationStarted) {
        log.info("Koin started.")
    }
    monitor.subscribe(KoinApplicationStopPreparing) {
        log.info("Koin stopping...")
    }
    monitor.subscribe(KoinApplicationStopped) {
        log.info("Koin stopped.")
    }

    val helloService by inject<HelloService>()
    // Routing section
    routing {
        get("/hello") {
            val scopeComponent = call.scope.get<ScopeComponent>()
            val newId = scopeComponent.id
            println("ScopeComponent id = $newId")
            println("ScopeComponent call = $${scopeComponent.call}")
            assert(Counter.init == 1)
            call.respondText(helloService.sayHello())
        }
    }
}

