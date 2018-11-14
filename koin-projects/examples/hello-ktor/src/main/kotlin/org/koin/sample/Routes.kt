package org.koin.sample

import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.get
import org.koin.ktor.ext.inject

fun Routing.v1() {

    // Lazy inject HelloService from within a Ktor Routing Node
    val service by inject<HelloService>()

    get("/v1/hello") {
        call.respondText("[/v1/hello] " + service.sayHello())
    }
}

fun Route.bye() {

    // Lazy inject HelloService from within a Ktor Route
    val service by inject<HelloService>()

    get("/v1/bye") {
        call.respondText("[/v1/bye] " + service.sayHello())
    }
}
