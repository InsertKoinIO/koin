package org.koin.ktor.di

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import org.junit.Test
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import kotlin.test.assertEquals

class KtorDIBridgeTest {

    @Test
    fun `should access Koin dependency via inject delegate`() = testApplication {
        application {
            bridgeModule()
        }

        val response = client.get("/koin")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello from Koin!", response.bodyAsText())
    }

    @Test
    fun `should access Ktor DI dependency via dependencies delegate`() = testApplication {
        application {
            bridgeModule()
        }

        val response = client.get("/ktor-di")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Processed by Ktor DI", response.bodyAsText())
    }

    @Test
    fun `should access both dependencies in mixed mode via dependencies delegate`() = testApplication {
        application {
            bridgeModule()
        }

        val response = client.get("/mixed-ktor-di")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello from Koin! - Processed by Ktor DI", response.bodyAsText())
    }

    @Test
    fun `should access both dependencies in mixed mode via inject delegate`() = testApplication {
        application {
            bridgeModule()
        }

        val response = client.get("/mixed-koin")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello from Koin! - Processed by Ktor DI", response.bodyAsText())
    }

    private fun Application.bridgeModule() {
        // Install Koin first
        install(Koin) {
            modules(module {
                single<HelloService> { HelloServiceImpl() }
            })
        }

        // Install Ktor DI and register dependencies
        dependencies {
            provide<KtorSpecificService> { KtorSpecificServiceImpl() }
        }

        routing {
            get("/koin") {
                val helloService: HelloService by inject() // From koin
                call.respond(helloService.sayHello())
            }

            get("/ktor-di") {
                val ktorService: KtorSpecificService by dependencies // From Ktor DI
                call.respond(ktorService.process())
            }

            get("/mixed-ktor-di") {
                // Using both Koin and Ktor DI via dependencies delegate
                val helloService: HelloService by dependencies // From Koin via Ktor DI
                val ktorService: KtorSpecificService by dependencies // From Ktor DI
                try {
                    call.respond("${helloService.sayHello()} - ${ktorService.process()}")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error: ${e.message}")
                }

            }

            get("/mixed-koin") {
                // Using both Koin and Ktor DI via inject delegate
                val helloService: HelloService by inject() // From Koin
                val ktorService: KtorSpecificService by inject() // From Ktor DI via Koin
                try {
                    call.respond("${helloService.sayHello()} - ${ktorService.process()}")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error: ${e.message}")
                }
            }
        }
    }

}

interface HelloService {
    fun sayHello(): String
}

class HelloServiceImpl : HelloService {
    override fun sayHello(): String = "Hello from Koin!"
}

interface KtorSpecificService {
    fun process(): String
}

class KtorSpecificServiceImpl : KtorSpecificService {
    override fun process(): String = "Processed by Ktor DI"
}