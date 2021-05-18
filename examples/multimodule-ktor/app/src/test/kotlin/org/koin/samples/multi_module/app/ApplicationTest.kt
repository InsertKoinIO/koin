package org.koin.samples.multi_module.app

import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.Test
import org.koin.test.AutoCloseKoinTest
import kotlin.test.assertEquals

internal class ApplicationTest : AutoCloseKoinTest() {
    private val expectMessageA = "Hello! I am org.koin.samples.multi_module.module_a.ModuleAService"
    private val expectMessageB = "Hello! I am org.koin.samples.multi_module.module_b.ModuleBService"

    @Test
    fun `test only module A`() = withTestApplication(Application::main) {
        with(handleRequest(HttpMethod.Get, "/module-a")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(expectMessageA, response.content)
        }

        with(handleRequest(HttpMethod.Get, "/all/only-a")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(expectMessageA, response.content)
        }
    }

    @Test
    fun `test only module B`() = withTestApplication(Application::main) {
        with(handleRequest(HttpMethod.Get, "/module-b")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(expectMessageB, response.content)
        }

        with(handleRequest(HttpMethod.Get, "/all/only-b")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(expectMessageB, response.content)
        }

    }

    @Test
    fun `test all`() = withTestApplication(Application::main) {
        with(handleRequest(HttpMethod.Get, "/all")) {
            assertEquals(HttpStatusCode.OK, response.status())
            // assertEquals("$expectMessageA\n$expectMessageB", response.content)
        }
    }

}
