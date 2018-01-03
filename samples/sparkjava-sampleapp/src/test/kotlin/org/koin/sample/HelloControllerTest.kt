package org.koin.sample

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.sample.util.SparkTestUtil
import org.koin.sample.util.startSpark
import org.koin.sample.util.stopSpark
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.KoinTest

class HelloControllerTest : KoinTest {

    lateinit var sparkTest: SparkTestUtil

    @Before()
    fun before() {
        val port = startSpark(0) {
            startKoin(listOf(helloAppModule))
            get<HelloController>()
        }
        sparkTest = SparkTestUtil(port)
    }

    @After
    fun after() {
        closeKoin()
        stopSpark()
    }

    @Test
    fun `controller say hello`() {
        val response = sparkTest.get("/hello")
        assertEquals(200, response.status)
        assertEquals("Hello Spark & Koin !", response.body)
    }
}