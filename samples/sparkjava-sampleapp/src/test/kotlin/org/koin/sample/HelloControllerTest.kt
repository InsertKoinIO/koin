package org.koin.sample

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.sample.util.SparkTestUtil
import org.koin.sample.util.start
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.KoinTest
import spark.kotlin.stop

class HelloControllerTest : KoinTest {

    lateinit var sparkTest: SparkTestUtil

    @Before()
    fun before() {
        val port = start(0) {
            startKoin(listOf(helloAppModule))
            get<HelloController>()
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
    fun `controller say hello`() {
        val response = sparkTest.get("/hello")
        assertEquals(200, response.status)
        assertEquals("Hello Spark & Koin !", response.body)
    }
}