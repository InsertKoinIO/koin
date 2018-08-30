package fr.ekito.myweatherapp

import android.content.Context
import fr.ekito.myweatherapp.di.offlineWeatherApp
import fr.ekito.myweatherapp.di.onlineWeatherApp
import fr.ekito.myweatherapp.di.testWeatherApp
import org.junit.After
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.test.KoinTest
import org.koin.test.checkModules
import org.mockito.Mockito.mock

/**
 * Test Koin modules
 */
class CheckConfigTest : KoinTest {

    @After
    fun after() {
        stopKoin()
    }

    val mockAndroid = module {
        single { mock(Context::class.java) }
    }

    @Test
    fun testRemoteConfiguration() {
        checkModules(onlineWeatherApp + mockAndroid)
    }

    @Test
    fun testLocalConfiguration() {
        checkModules(offlineWeatherApp + mockAndroid)
    }

    @Test
    fun testTestConfiguration() {
        checkModules(testWeatherApp + mockAndroid)
    }
}