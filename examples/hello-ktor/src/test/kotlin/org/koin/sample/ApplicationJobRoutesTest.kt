package org.koin.sample

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import org.junit.Before
import org.junit.Test
import org.koin.test.AutoCloseKoinTest
import kotlin.test.Ignore
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ApplicationJobRoutesTest : AutoCloseKoinTest() {

    @Before
    fun before(){
        Counter.init = 0
    }

    //TODO Tests are failing
//    @Test
//    fun testHelloRequest() = testApplication {
//        val response = client.get("/hello")
//        assertEquals(HttpStatusCode.OK, response.status)
//        assert(response.bodyAsText().contains("Hello Ktor & Koin!"))
//
//        assertEquals(HttpStatusCode.NotFound, client.get("/").status)
//    }
//
//    @Test
//    fun testHelloV1Request() = testApplication {
//        val response = client.get("/v1/hello")
//        assertEquals(HttpStatusCode.OK, response.status)
//        assert(response.bodyAsText().contains("[/v1/hello] Hello Again!"))
//    }
}
