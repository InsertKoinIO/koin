package org.koin.sample

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.module.dsl.singleOf
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.koinModule

fun Application.module2() {
    koinModule {
            singleOf(::HelloService2)
    }

    // Lazy inject HelloService from within a Ktor Routing Node
    val service by inject<HelloService2>()
    // Routing section
    routing {
        get("/v1/hello") {
            call.respondText("[/v1/hello] " + service.sayHello())
        }
    }
}
