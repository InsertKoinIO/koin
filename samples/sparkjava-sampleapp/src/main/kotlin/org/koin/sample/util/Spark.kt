package org.koin.sample.util

import org.koin.Koin
import spark.Spark
import spark.kotlin.after
import spark.kotlin.port
import spark.kotlin.stop

fun startSpark(port: Int = 4567, runControllers: () -> Unit): Int {

    port(port)

    // Logging filter
    after("*") {
        println(request.requestMethod() + " " + request.pathInfo() + " - " + response.raw().status)
    }

    Koin.logger = Log4JLogger()

    // launch controllers initialization
    runControllers()

    // This is the important line. It must be *after* creating the routes and *before* the call to port()
    Spark.awaitInitialization()

    // Will return the automatically defined port if requested port was 0 (useful for testing)
    return port()
}

fun stopSpark() {
    stop()

    // Need to sleep in order to let the server stops
    // It's done in another thread (cf. spark.Service.stop())
    Thread.sleep(100)
}