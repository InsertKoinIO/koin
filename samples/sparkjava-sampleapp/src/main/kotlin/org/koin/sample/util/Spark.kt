package org.koin.sample.util

import spark.Spark
import spark.kotlin.after
import spark.kotlin.port

fun start(port: Int = 4567, controllersInit: () -> Unit): Int {

    port(port)

    // Logging filter
    after("*") {
        println(request.requestMethod() + " " + request.pathInfo() + " - " + response.raw().status)
    }

    // launch controllers initialization
    controllersInit()

    // This is the important line. It must be *after* creating the routes and *before* the call to port()
    Spark.awaitInitialization()

    // Will return the automatically defined port if requested port was 0 (useful for testing)
    return port()
}