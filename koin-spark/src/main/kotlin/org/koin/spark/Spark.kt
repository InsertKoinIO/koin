package org.koin.spark

import org.koin.Koin
import org.koin.KoinContext
import org.koin.dsl.module.Module
import org.koin.standalone.StandAloneContext
import org.koin.standalone.StandAloneContext.closeKoin
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
fun start(port: Int = DEFAULT_PORT, modules: List<Module>, controllers: () -> Unit): Int {

    Koin.logger = Log4JLogger()

    // Start Koin
    startKoin(modules, useEnvironmentProperties = true)
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
    controllers()

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