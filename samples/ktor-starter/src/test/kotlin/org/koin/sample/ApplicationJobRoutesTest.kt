package org.koin.sample

import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.Before
import org.junit.Test
import org.koin.Koin
import org.koin.log.PrintLogger
import org.koin.sample.Properties.BYE_MSG
import org.koin.sample.Properties.HELLO_MSG
import org.koin.sample.Properties.MY_MODEL
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.getProperty
import org.koin.test.AbstractKoinTest
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ApplicationJobRoutesTest : AbstractKoinTest() {

    @Before
    fun before() {
        Koin.logger = PrintLogger()
        startKoin(listOf(KoinModule),
                properties = mapOf(HELLO_MSG to "Bonjour", BYE_MSG to "Au revoir", MY_MODEL to Model("Test value")))
    }

    @Test
    fun testRootRequest() = withTestApplication(Application::jobRoutes) {

        with(handleRequest(HttpMethod.Get, "/")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Hello, World from Ktor and Koin!", response.content)
        }

        with(handleRequest(HttpMethod.Get, "/index.html")) {
            assertFalse(requestHandled)
        }
    }

    @Test
    fun testHiRequest() = withTestApplication(Application::jobRoutes) {

        with(handleRequest(HttpMethod.Get, "/hi")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Bonjour from Ktor and Koin", response.content)
        }
    }

    @Test
    fun testByeRequest() = withTestApplication(Application::jobRoutes) {

        with(handleRequest(HttpMethod.Get, "/bye")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Au revoir from Ktor and Koin", response.content)
        }
    }

    @Test
    fun testModelRequest() = withTestApplication(Application::jobRoutes) {

        with(handleRequest(HttpMethod.Get, "/model")) {
            assertEquals(HttpStatusCode.OK, response.status())
            val currentModel = getProperty<Model>(MY_MODEL)

            assertEquals("Test value", currentModel.value)
            assertEquals("Model value = ${currentModel.value}", response.content)
        }

        with(handleRequest(HttpMethod.Get, "/hi")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Bonjour from Ktor and Koin", response.content)
        }

        with(handleRequest(HttpMethod.Get, "/model")) {
            assertEquals(HttpStatusCode.OK, response.status())
            val currentModel = getProperty<Model>(MY_MODEL)

            assertEquals("Hi already said !", currentModel.value)
            assertEquals("Model value = ${currentModel.value}", response.content)
        }
    }
}
