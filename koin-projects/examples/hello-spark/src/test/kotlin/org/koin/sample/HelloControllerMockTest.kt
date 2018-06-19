package org.koin.sample

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.sample.util.SparkTestUtil
import org.koin.spark.start
import org.koin.spark.stop
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

class HelloControllerMockTest : KoinTest {

    lateinit var sparkTest: SparkTestUtil

    val mockService: HelloService by inject()

    @Before()
    fun before() {
        val port = start(0, modules = listOf(helloMockModule)) {
            HelloController(mockService)
        }
        sparkTest = SparkTestUtil(port)
    }

    @After
    fun after() {
        stop(300)
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
}