package org.koin.sample.ktor.di

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

fun main() {
    embeddedServer(Netty, port = 8080) {
        mainModule()
    }.start(wait = true)
}

fun Application.mainModule() {
    install(CallLogging)

    install(Koin) {
        printLogger(Level.DEBUG)

        bridge {
            ktorToKoin()
            koinToKtor()
        }

        modules(module {
            single<HelloKoinService> { HelloKoinServiceImpl() }
        })
    }

    dependencies {
        provide<KtorSpecificService> { KtorSpecificServiceImpl() }
    }

    routing {
        get("/koin") {
            val helloKoinService: HelloKoinService by inject() // From koin
            call.respondText(helloKoinService.sayHello())
        }
        
        get("/ktor-di") {
            val ktorService: KtorSpecificService by dependencies // From Ktor DI
            call.respondText(ktorService.process())
        }
        
        get("/mixed-ktor-di") {
            val helloKoinService: HelloKoinService by dependencies // From Koin via Ktor DI
            val ktorService: KtorSpecificService by dependencies // From Ktor DI
            
            call.respondText("${helloKoinService.sayHello()} - ${ktorService.process()}")
        }

        get("/mixed-koin") {
            val helloKoinService: HelloKoinService by inject() // From Koin
            val ktorService: KtorSpecificService by inject() // From Ktor Di via Koin

            call.respondText("${helloKoinService.sayHello()} - ${ktorService.process()}")
        }
    }
}