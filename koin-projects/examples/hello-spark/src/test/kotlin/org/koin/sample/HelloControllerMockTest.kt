package org.koin.sample

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.sample.Constants.WAIT_STOP
import org.koin.sample.util.SparkTestUtil
import org.koin.spark.start
import org.koin.spark.stop
import org.koin.standalone.get
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.koin.test.createMock
import org.mockito.Mockito.*

class HelloControllerMockTest : KoinTest {

    lateinit var sparkTest: SparkTestUtil

    val mockService: HelloService by inject()

    @Before()
    fun before() {
        stop(WAIT_STOP)
        val port = start(0, modules = emptyList()) {
            createMock<HelloService>()
            HelloController(get())
        }

        sparkTest = SparkTestUtil(port)
    }

    @After
    fun after() {
        stop(WAIT_STOP)
    }

    @Test
    fun `controller say hello with mock`() {
        val emptyResponse = ""
        `when`(mockService.sayHello()).thenReturn(emptyResponse)

        val response = sparkTest.get("/hello")
        assertEquals(200, response.status)
        assertEquals(emptyResponse, response.body)

        verify(mockService).sayHello()
    }

    @Test
    fun `controller don't say hello with mock`() {
        val emptyResponse = ""
        `when`(mockService.sayHello()).thenReturn(emptyResponse)

        val response = sparkTest.get("/hellooo")
        assertEquals(404, response.status)

        verify(mockService, never()).sayHello()
    }
}