package org.koin.sample

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.ktor.plugin.KoinApplicationStarted
import org.koin.ktor.plugin.KoinApplicationStopPreparing
import org.koin.ktor.plugin.KoinApplicationStopped
import org.koin.logger.slf4jLogger

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080) {
        mainModule()
        module2()
    }.start(wait = true)
}

fun Application.mainModule() {
    install(CallLogging)
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

    // Install Ktor features
    environment.monitor.subscribe(KoinApplicationStarted) {
        log.info("Koin started.")
    }
    environment.monitor.subscribe(KoinApplicationStopPreparing) {
        log.info("Koin stopping...")
    }
    environment.monitor.subscribe(KoinApplicationStopped) {
        log.info("Koin stopped.")
    }

    val helloService by inject<HelloService>()
    // Routing section
    routing {
        get("/hello") {
            call.respondText(helloService.sayHello())
        }
    }
}


