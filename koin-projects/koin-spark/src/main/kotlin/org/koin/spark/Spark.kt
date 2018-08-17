/*
 * Copyright 2017-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.koin.spark

import org.koin.core.KoinContext
import org.koin.dsl.module.Module
import org.koin.log.Logger.SLF4JLogger
import org.koin.standalone.StandAloneContext
import org.koin.standalone.StandAloneContext.createEagerInstances
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.StandAloneContext.stopKoin
import spark.Spark
import spark.kotlin.after
import spark.kotlin.port
import spark.kotlin.stop


const val DEFAULT_PORT = 0

/**
 * Start Spark server
 * @param port - server port /default 4567
 * @param controllers - function to run Koin
 *
 * @author Arnaud Giuliani
 */
fun start(port: Int = DEFAULT_PORT, modules: List<Module>, controllers: (() -> Unit)? = null): Int {

    // launch controllers initialization
    startKoin(
        modules,
        useEnvironmentProperties = true,
        useKoinPropertiesFile = true,
        logger = SLF4JLogger()
    )

    // Get port from properties
    val foundPort =
        (StandAloneContext.koinContext as KoinContext).getProperty("server.port", "4567").toInt()
    if (port != DEFAULT_PORT) {
        port(port)
    } else {
        port(foundPort)
    }

    if (controllers != null) {
        // Start Koin
        controllers()
    } else {
        // Create Koin instances
        createEagerInstances()
    }


    // Logging filter
    after("*") {
        println(request.requestMethod() + " " + request.pathInfo() + " - " + response.raw().status)
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
    stopKoin()
    stop()

    // Need to sleep in order to let the server stops
    // It's done in another thread (cf. spark.Service.stop())
    Thread.sleep(sleep)
}