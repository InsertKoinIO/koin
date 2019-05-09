package org.koin.sample

import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.Test
import org.koin.test.AutoCloseKoinTest
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ApplicationJobRoutesTest : AutoCloseKoinTest() {

    @Test
    fun testHelloRequest() = withTestApplication(Application::main) {
        with(handleRequest(HttpMethod.Get, "/hello")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Hello Ktor & Koin !", response.content)
        }

        with(handleRequest(HttpMethod.Get, "/index.html")) {
            assertFalse(requestHandled)
        }
    }

    @Test
    fun testV1HelloRequest() = withTestApplication(Application::main) {
        with(handleRequest(HttpMethod.Get, "/v1/hello")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("[/v1/hello] Hello Ktor & Koin !", response.content)
        }

        with(handleRequest(HttpMethod.Get, "/index.html")) {
            assertFalse(requestHandled)
        }
    }

    @Test
    fun testV1ByeRequest() = withTestApplication(Application::main) {
        with(handleRequest(HttpMethod.Get, "/v1/bye")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("[/v1/bye] Hello Ktor & Koin !", response.content)
        }

        with(handleRequest(HttpMethod.Get, "/index.html")) {
            assertFalse(requestHandled)
        }
    }
}
