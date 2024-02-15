package org.koin.samples.multi_module.app

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.ktor.ext.getKoin
import org.koin.ktor.ext.inject
import org.koin.ktor.ext.koin
import org.koin.logger.slf4jLogger
import org.koin.samples.multi_module.common.IService
import org.koin.samples.multi_module.module_a.ModuleAService
import org.koin.samples.multi_module.module_a.moduleA
import org.koin.samples.multi_module.module_b.ModuleBService
import org.koin.samples.multi_module.module_b.moduleB

fun Application.main() {
    install(CallLogging)
    koin {
        slf4jLogger()
    }

    moduleA()
    moduleB()

    routing {
        val services = getKoin().getAll<IService>()
        val serviceA by inject<ModuleAService>()
        val serviceB by inject<ModuleBService>()

        get("/all") {
            val response = services.map { it.sayHello() }.sorted().joinToString(separator = "\n")
            call.respondText(response)
        }

        get("/all/only-a") {
            call.respondText(serviceA.sayHello())
        }

        get("/all/only-b") {
            call.respondText(serviceB.sayHello())
        }
    }
}

fun main(args: Array<String>) {
    // Start Ktor
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}
