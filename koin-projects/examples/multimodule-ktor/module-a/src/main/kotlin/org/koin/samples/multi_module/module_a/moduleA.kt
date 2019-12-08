package org.koin.samples.multi_module.module_a

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.experimental.builder.single
import org.koin.ktor.ext.inject
import org.koin.ktor.ext.koin
import org.koin.samples.multi_module.common.IService

fun Application.moduleA() {
    koin {
        modules(module {
            single<ModuleAService>() bind IService::class
        })
    }
    routing {
        val service by inject<ModuleAService>()
        get("/module-a") {
            call.respondText(service.sayHello())
        }
    }
}