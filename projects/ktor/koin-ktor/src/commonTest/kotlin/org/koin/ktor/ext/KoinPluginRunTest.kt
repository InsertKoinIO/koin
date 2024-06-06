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

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.CIO
import io.ktor.server.engine.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KoinPluginRunTest {

    @BeforeTest
    fun before(){
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
    @Ignore // socket exception on GH
    fun `run outside context`()  = runBlocking<Unit> {
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
            CIO,
            module = {
                val test by inject<String>()
                println(test)
            },
        ).start(false)

        delay(500)
        s.stop()
        assertTrue("counter should 1 - instance is created") { counter == 1 }
    }
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

private fun testMyApplicationNoKoin(test: suspend (jsonClient: HttpClient) -> Unit) = testApplication {
    application {

    }
    test.invoke(createClient {})
}

class KtorMyModule(application: Application) {
    init {
        application.routing {
            get("testurl") { call.respond(HttpStatusCode.OK, "Test response") }
        }
    }
}