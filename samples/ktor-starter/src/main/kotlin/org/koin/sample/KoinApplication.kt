package org.koin.sample

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.Koin
import org.koin.dsl.module.applicationContext
import org.koin.log.PrintLogger
import org.koin.sample.Properties.BYE_MSG
import org.koin.sample.Properties.MY_MODEL
import org.koin.standalone.StandAloneContext.startKoin

fun main(args: Array<String>) {
    // Initialize Koin logger
    Koin.logger = PrintLogger()

    // Start Koin
    startKoin(arrayListOf(KoinModule), properties = mapOf(BYE_MSG to "See you soon", MY_MODEL to Model("Initial value")))

    embeddedServer(Netty, commandLineEnvironment(args)).start()
}

/**
 * Defines main Ktor application configuration and setup Koin DI framework
 */
fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
}

/**
 * Sample data class Model
 */
data class Model(val value: String)

object Properties {
    const val HELLO_MSG = "HELLO_MSG"
    const val BYE_MSG = "BYE_MSG"
    const val MY_MODEL = "MY_MODEL"
}

/**
 * Koin module used to describe DI context using Koin DSL.
 */
val KoinModule = applicationContext {
    // Business Service binding definition
    provide { BusinessServiceImpl() } bind BusinessService::class
}
