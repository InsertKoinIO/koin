package org.koin.spark

import org.koin.core.KoinContext
import org.koin.core.parameter.emptyParameterDefinition
import org.koin.dsl.module.Module
import org.koin.log.Logger.SLF4JLogger
import org.koin.standalone.StandAloneContext
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.createEagerInstances
import org.koin.standalone.StandAloneContext.startKoin
import spark.Spark
import spark.kotlin.after
import spark.kotlin.port
import spark.kotlin.stop

val DEFAULT_PORT = 0

/**
 * Start Spark server
 * @param port - server port /default 4567
 * @param controllers - function to run Koin
 */
fun start(port: Int = DEFAULT_PORT, modules: List<Module>, controllers: (() -> Unit)? = null): Int {

    // Start Koin
    startKoin(modules, useEnvironmentProperties = true, logger = SLF4JLogger())
    // Get port from properties
    val foundPort = (StandAloneContext.koinContext as KoinContext).getProperty("server.port", "4567").toInt()
    if (port != DEFAULT_PORT) {
        port(port)
    } else {
        port(foundPort)
    }

    // Logging filter
    after("*") {
        println(request.requestMethod() + " " + request.pathInfo() + " - " + response.raw().status)
    }

    // launch controllers initialization
    if (controllers != null) {
        controllers()
    } else {
        createEagerInstances(emptyParameterDefinition())
    }

    // This is the important line. It must be *after* creating the routes and *before* the call to port()
    Spark.awaitInitialization()

    // Will return the automatically defined port if requested port was 0 (useful for testing)
    return port()
}

/**
 * Stop spark server and wait
 */
fun stop(sleep: Long = 100) {
    stop()
    closeKoin()

    // Need to sleep in order to let the server stops
    // It's done in another thread (cf. spark.Service.stop())
    Thread.sleep(sleep)
}

/**
 * Run all Spark controllers (function)
 */
fun runControllers() {
    (org.koin.standalone.StandAloneContext.koinContext as KoinContext).runSparkControllers()
}