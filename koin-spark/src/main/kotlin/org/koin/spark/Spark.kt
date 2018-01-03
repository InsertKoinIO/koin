package org.koin.spark

import org.koin.Koin
import spark.Spark
import spark.kotlin.after
import spark.kotlin.port
import spark.kotlin.stop

/**
 * Start Spark server
 * @param port - server port /default 4567
 * @param runControllers - function to run Koin
 */
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

/**
 * Stop spark server and wait
 */
fun stopSpark(sleep: Long = 100) {
    stop()

    // Need to sleep in order to let the server stops
    // It's done in another thread (cf. spark.Service.stop())
    Thread.sleep(sleep)
}