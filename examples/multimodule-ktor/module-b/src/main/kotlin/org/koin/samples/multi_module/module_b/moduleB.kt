package org.koin.samples.multi_module.module_b

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.single
import org.koin.ktor.ext.inject
import org.koin.ktor.ext.modules
import org.koin.samples.multi_module.common.IService

fun Application.moduleB() {
    modules(
            module {
                single<ModuleBService>() bind IService::class
            }
    )
    routing {
        val service by inject<ModuleBService>()
        get("/module-b") {
            call.respondText(service.sayHello())
        }
    }

}