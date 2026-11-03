/*
 * Copyright 2017-2023 the original author or authors.
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
package org.koin.ktor.ext

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.CallFailed
import io.ktor.server.application.hooks.CallSetup
import io.ktor.server.application.hooks.ResponseSent
import io.ktor.server.application.install
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.core.scope.Scope
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.ktor.plugin.scope
import org.koin.module.requestScope
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KoinPluginRunTest {

    @Before
    fun before() {
        stopKoin()
    }

    @Test
    fun `minimalistic app run`() {
        testMyApplication {
            val response = it.get("testurl") {}

            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue { response.bodyAsText().contains("Test response") }
        }
    }

    @Test
    fun `sequential and concurrent requests get unique scope ids`() {
        testMyApplication { client ->
            suspend fun scopeId() = client.get("testurl").headers["X-Scope-Id"]

            val sequentialIds = (1..100).map { scopeId() }
            val concurrentIds = coroutineScope {
                (1..1_000).map { async(Dispatchers.Default) { scopeId() } }.awaitAll()
            }
            val ids = sequentialIds + concurrentIds

            assertEquals(ids.size, ids.toSet().size, "all request scope ids should be unique")
            assertTrue(
                ids.all { it?.removePrefix("request_")?.toLongOrNull() != null },
                "scope ids should be 'request_<number>'",
            )
        }
    }

    @Test
    fun `closes request scope on normal response completion`() = testApplication {
        lateinit var requestScope: Scope

        application {
            install(Koin)

            routing {
                get("/scope") {
                    requestScope = call.scope
                    call.respond(HttpStatusCode.OK)
                }
            }
        }

        client.get("/scope")

        val scopeClosed = requestScope.closed
        val scopeStillRegistered =
            requestScope.getKoin().getScopeOrNull(requestScope.id) != null

        assertTrue(
            scopeClosed && !scopeStillRegistered,
            "Koin-created request scope should be closed and removed " +
                "(closed=$scopeClosed, registered=$scopeStillRegistered)",
        )
    }

    @Test
    fun `closes request scope when a later response completion hook fails`() = testApplication {
        lateinit var requestScope: Scope
        val responseCompletionHookRan = AtomicBoolean()

        application {
            install(Koin)

            // A hook installed later runs first after proceed() in the
            // ResponseSent phase.
            install(
                createApplicationPlugin(
                    name = "FailResponseCompletionBeforeKoinCleanup",
                ) {
                    on(ResponseSent) {
                        responseCompletionHookRan.set(true)
                        error("response completion failed before Koin cleanup")
                    }
                },
            )

            routing {
                get("/scope") {
                    requestScope = call.scope
                    call.respond(HttpStatusCode.OK)
                }
            }
        }

        runCatching {
            client.get("/scope")
        }

        assertTrue(
            responseCompletionHookRan.get(),
            "response completion failure hook should run",
        )

        val scopeClosed = requestScope.closed
        val scopeStillRegistered =
            requestScope.getKoin().getScopeOrNull(requestScope.id) != null

        assertTrue(
            scopeClosed && !scopeStillRegistered,
            "Koin-created request scope should be closed and removed " +
                "(closed=$scopeClosed, registered=$scopeStillRegistered)",
        )
    }

    @Test
    fun `closes request scope when route handler fails before responding`() = testApplication {
        lateinit var requestScope: Scope

        // like a real engine: answer the unhandled exception instead of rethrowing it into the test
        environment { config = MapApplicationConfig("ktor.test.throwOnException" to "false") }
        application {
            install(Koin)

            routing {
                get("/fail") {
                    requestScope = call.scope
                    error("route handler failed before responding")
                }
            }
        }

        // the engine answers the unhandled exception with its own error response
        runCatching {
            client.get("/fail")
        }

        val scopeClosed = requestScope.closed
        val scopeStillRegistered =
            requestScope.getKoin().getScopeOrNull(requestScope.id) != null

        assertTrue(
            scopeClosed && !scopeStillRegistered,
            "Koin-created request scope should be closed and removed " +
                "(closed=$scopeClosed, registered=$scopeStillRegistered)",
        )
    }

    @Test
    @Ignore("socket exception on GH")
    fun `run outside context`() = runBlocking<Unit> {
        var counter = 0
        startKoin {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single<String> {
                        counter++
                        "Reproduction test"
                    }
                }
            )
        }

        val s = embeddedServer(
            Netty,
            module = {
                val test by inject<String>()
                println(test)
            },
        ).start(false)

        delay(500)
        s.stop()
        assert(counter == 1) { "counter should 1 - instance is created" }
    }

    @Test
    @Ignore("socket exception on GH")
    fun `should can reload`() = runBlocking<Unit> {
        val koinModule = module {
            single<String> {
                "Reproduction test"
            }
        }
        val s = embeddedServer(
            Netty,
            module = {
                install(Koin) {
                    modules(koinModule)
                }
            },
        ).start(false)
        delay(500)

        // verify for can auto-reload
        s.reload()
        s.stop()
    }

    @Test
    fun `request scope is closed when the call is cancelled`() = testApplication {
        val engineCall = CompletableDeferred<ApplicationCall>()
        application {
            install(Koin)
            install(createApplicationPlugin("CaptureCall") {
                on(CallSetup) { call -> engineCall.complete(call) }
            })
            routing {
                get("cancelled") {
                    // What the engine does when the client goes away: the call job is cancelled, no response is sent
                    engineCall.await().coroutineContext[Job]!!.cancel()
                    awaitCancellation()
                }
            }
        }

        runCatching { client.get("cancelled") }

        val call = engineCall.await()
        call.coroutineContext[Job]!!.join()
        val scope = call.scope
        assertTrue(scope.closed, "request scope should be closed")
        assertNull(scope.getKoin().getScopeOrNull(scope.id), "request scope should be removed from the registry")
    }

    // Production pipelines run on a different executor than the test host's default development mode.
    @Test
    fun `cancelled call closes its request scope in production mode`() = testApplication {
        val engineCall = CompletableDeferred<ApplicationCall>()
        serverConfig { developmentMode = false }
        application {
            install(Koin)
            install(createApplicationPlugin("CaptureCall") { on(CallSetup) { call -> engineCall.complete(call) } })
            routing {
                get("cancelled") {
                    engineCall.await().coroutineContext[Job]!!.cancel()
                    awaitCancellation()
                }
            }
        }

        runCatching { client.get("cancelled") }

        val call = engineCall.await()
        call.coroutineContext[Job]!!.join()
        assertTrue(call.scope.closed, "request scope should be closed")
    }

    // StatusPages-style error page installed before Koin, so its CallFailed handler wraps Koin's pipeline phases.
    @Test
    fun `error page installed before Koin can resolve request-scoped dependencies`() = testApplication {
        application {
            install(createApplicationPlugin("ErrorPage") {
                on(CallFailed) { call, _ -> call.respondText("error:" + call.scope.get<RequestScopedDependency>().label) }
            })
            install(Koin) { modules(module { requestScope { scoped { RequestScopedDependency() } } }) }
            routing { get("/fail") { error("route handler failed") } }
        }

        assertEquals("error:request-scoped", client.get("/fail").bodyAsText())
    }

    @Test
    fun `engine error response can still resolve request-scoped dependencies`() = testApplication {
        val resolvedDuringErrorResponse = CompletableDeferred<String>()
        lateinit var requestScope: Scope
        environment { config = MapApplicationConfig("ktor.test.throwOnException" to "false") }
        application {
            install(Koin) { modules(module { requestScope { scoped { RequestScopedDependency() } } }) }
            install(createApplicationPlugin("ResponseAudit") {
                on(ResponseSent) { call ->
                    resolvedDuringErrorResponse.complete(runCatching { call.scope.get<RequestScopedDependency>().label }.getOrElse { it.toString() })
                }
            })
            routing { get("/fail") { requestScope = call.scope; error("route handler failed") } }
        }

        assertEquals(HttpStatusCode.InternalServerError, client.get("/fail").status)
        assertEquals("request-scoped", resolvedDuringErrorResponse.await())
        assertTrue(requestScope.closed, "request scope should be closed once the error response is sent")
    }

    // withTimeout's TimeoutCancellationException does not cancel the call: the engine still answers it with a 504.
    @Test
    fun `timed out handler still answers with an open request scope`() = testApplication {
        val resolvedDuringErrorResponse = CompletableDeferred<String>()
        lateinit var requestScope: Scope
        application {
            install(Koin) { modules(module { requestScope { scoped { RequestScopedDependency() } } }) }
            install(createApplicationPlugin("ResponseAudit") {
                on(ResponseSent) { call ->
                    resolvedDuringErrorResponse.complete(runCatching { call.scope.get<RequestScopedDependency>().label }.getOrElse { it.toString() })
                }
            })
            routing { get("/slow") { requestScope = call.scope; withTimeout(20) { delay(1_000) } } }
        }

        assertEquals(HttpStatusCode.GatewayTimeout, client.get("/slow").status)
        assertEquals("request-scoped", resolvedDuringErrorResponse.await())
        assertTrue(requestScope.closed, "request scope should be closed once the 504 is sent")
    }

    // The engine's own error response is sent after the application pipeline exited; a hook failing there
    // skips Koin's ResponseSent close.
    @Test
    fun `engine error response failing after commit closes the request scope`() = testApplication {
        lateinit var requestScope: Scope
        environment { config = MapApplicationConfig("ktor.test.throwOnException" to "false") }
        application {
            install(Koin)
            install(createApplicationPlugin("FailingOnServerError") {
                on(ResponseSent) { call -> if (call.response.status() == HttpStatusCode.InternalServerError) error("response hook failed") }
            })
            routing { get("/fail") { requestScope = call.scope; error("route handler failed") } }
        }

        runCatching { client.get("/fail") }

        assertTrue(requestScope.closed, "request scope should be closed")
        assertNull(requestScope.getKoin().getScopeOrNull(requestScope.id), "request scope should be removed from the registry")
    }

    // A response hook failing before commit fails the engine's error response too, which then answers through a
    // fallback outside the send pipeline: ResponseSent never fires.
    @Test
    fun `response hook failing before commit closes the request scope`() = testApplication {
        lateinit var requestScope: Scope
        environment { config = MapApplicationConfig("ktor.test.throwOnException" to "false") }
        application {
            install(Koin) { modules(module { requestScope { scoped<RequestScopedDependency> { error("unknown tenant") } } }) }
            install(createApplicationPlugin("TenantHeader") {
                onCallRespond { call, _ -> call.response.headers.append("X-Tenant", call.scope.get<RequestScopedDependency>().label) }
            })
            routing { get("/ok") { requestScope = call.scope; call.respondText("ok") } }
        }

        runCatching { client.get("/ok") }

        assertTrue(requestScope.closed, "request scope should be closed")
        assertNull(requestScope.getKoin().getScopeOrNull(requestScope.id), "request scope should be removed from the registry")
    }

    @Test
    fun `error page installed after Koin can resolve request-scoped dependencies`() = testApplication {
        application {
            install(Koin) { modules(module { requestScope { scoped { RequestScopedDependency() } } }) }
            install(createApplicationPlugin("ErrorPage") {
                on(CallFailed) { call, _ -> call.respondText("error:" + call.scope.get<RequestScopedDependency>().label) }
            })
            routing { get("/fail") { error("route handler failed") } }
        }

        assertEquals("error:request-scoped", client.get("/fail").bodyAsText())
    }
}

class RequestScopedDependency {
    val label = "request-scoped"
}

private fun testMyApplication(test: suspend (jsonClient: HttpClient) -> Unit) = testApplication {
    application {
        install(Koin) {
            modules(
                module {
                    single { this@application }
                    single(createdAtStart = true) { KtorMyModule(get()) }
                },
            )
        }
    }
    test.invoke(createClient {})
}

private fun testMyApplicationNoKoin(test: suspend (jsonClient: HttpClient) -> Unit) =
    testApplication {
        application {

        }
        test.invoke(createClient {})
    }

class KtorMyModule(application: Application) {
    init {
        application.routing {
            get("testurl") {
                call.response.headers.append("X-Scope-Id", call.scope.id)
                call.respond(HttpStatusCode.OK, "Test response")
            }
        }
    }
}