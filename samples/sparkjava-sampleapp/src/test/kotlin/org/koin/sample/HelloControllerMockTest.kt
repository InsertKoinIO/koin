package org.koin.sample

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.sample.util.SparkTestUtil
import org.koin.sample.util.start
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import spark.kotlin.stop

class HelloControllerMockTest : KoinTest {

    lateinit var sparkTest: SparkTestUtil

    val mockService: HelloService by inject()

    @Before()
    fun before() {
        val port = start(0) {
            startKoin(listOf(helloMockModule))
            HelloController()
        }
        sparkTest = SparkTestUtil(port)
    }

    @After
    fun after() {
        stop()
        closeKoin()

        // Need to sleep in order to let the server stops
        // It's done in another thread (cf. spark.Service.stop())
        Thread.sleep(50)
    }

    @Test
    fun `controller say hello with mock`() {
        `when`(mockService.sayHello()).thenReturn("")

        val response = sparkTest.get("/hello")
        assertEquals(200, response.status)

        verify(mockService).sayHello()
    }
}