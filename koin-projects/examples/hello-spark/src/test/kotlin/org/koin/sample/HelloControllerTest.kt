//package org.koin.sample
//
//import org.junit.After
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Test
//import org.koin.sample.Constants.WAIT_STOP
//import org.koin.sample.util.SparkTestUtil
//import org.koin.spark.start
//import org.koin.spark.stop
//import org.koin.test.KoinTest
//
//class HelloControllerTest : KoinTest {
//
//    lateinit var sparkTest: SparkTestUtil
//
//    @Before()
//    fun before() {
//        val port = start(0, modules = listOf(helloAppModule))
//        sparkTest = SparkTestUtil(port)
//    }
//
//    @After
//    fun after() {
//        stop(WAIT_STOP)
//    }
//
//    @Test
//    fun `controller say hello`() {
//        val response = sparkTest.get("/hello")
//        assertEquals(200, response.status)
//        assertEquals("Hello Spark & Koin !", response.body)
//    }
//}