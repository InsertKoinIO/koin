package org.koin.sample

import io.ktor.application.ApplicationCall
import io.ktor.response.respondText
import org.koin.ktor.ext.inject

suspend fun ApplicationCall.respondWithHello() {
    // Lazy inject HelloService from within an ApplicationCall
    val service by inject<HelloService>()
    respondText("[/v1/respondWithHello] " + service.sayHello())
}
