package org.koin.sample.ktor.di

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.junit.Test
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.logger.Level
import org.koin.ktor.ext.getKoin
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun `test koin endpoint`() = testApplication {
        application {
            mainModule()
        }

        @OptIn(KoinInternalApi::class)

        val response = client.get("/koin")

        assertEquals(HttpStatusCode.Companion.OK, response.status)
        assertEquals("Hello from Koin!", response.bodyAsText())
    }

    @Test
    fun `test ktor-di endpoint`() = testApplication {
        application {
            mainModule()
        }

        val response = client.get("/ktor-di")

        assertEquals(HttpStatusCode.Companion.OK, response.status)
        assertEquals("Processed by Ktor DI", response.bodyAsText())
    }

    @Test
    fun `test mixed-ktor-di endpoint`() = testApplication {
        application {
            mainModule()
        }

        val response = client.get("/mixed-ktor-di")

        assertEquals(HttpStatusCode.Companion.OK, response.status)
        assertEquals("Hello from Koin! - Processed by Ktor DI", response.bodyAsText())
    }

    @Test
    fun `test mixed-koin endpoint`() = testApplication {
        application {
            mainModule()
        }

        val response = client.get("/mixed-koin")

        assertEquals(HttpStatusCode.Companion.OK, response.status)
        assertEquals("Hello from Koin! - Processed by Ktor DI", response.bodyAsText())
    }

}