package org.koin.sample

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import org.koin.ktor.ext.inject
import org.koin.ktor.ext.property
import org.koin.ktor.ext.setProperty
import org.koin.sample.BusinessServiceImpl.Companion.BYE_JOB
import org.koin.sample.BusinessServiceImpl.Companion.HI_JOB
import org.koin.sample.KoinModule.Properties.MY_MODEL


/**
 * Defines sample routes implementation
 */
fun Application.jobRoutes() {

    // Inject service bean. Could alse be written : val service by inject<BusinessService>()
    val service by inject<BusinessService>()

    routing {

        get("/") {
            call.respondText("Hello, World from Ktor and Koin!")
        }

        get("/model") {
            // Inject lazily model object from properties
            val model by property<Model>(MY_MODEL)
            call.respondText("Model value = ${model.value}")
        }

        get("/hi") {
            // Set new property value
            setProperty(MY_MODEL, Model("Hi already said !"))
            call.respondText(service.doJob(HI_JOB))
        }

        get("/bye") {
            call.respondText(service.doJob(BYE_JOB))
        }
    }
}
