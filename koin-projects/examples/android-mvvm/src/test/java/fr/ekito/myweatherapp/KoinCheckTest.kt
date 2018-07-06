package fr.ekito.myweatherapp

import android.content.Context
import fr.ekito.myweatherapp.di.offlineWeatherApp
import fr.ekito.myweatherapp.di.onlineWeatherApp
import fr.ekito.myweatherapp.di.testWeatherApp
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.check
import org.mockito.Mockito.mock

/**
 * Test Koin modules
 */
class KoinCheckTest : AutoCloseKoinTest() {

    val mockAndroid = module {
        single { mock(Context::class.java) }
    }

    @Test
    fun testRemoteConfiguration() {
        check(onlineWeatherApp + mockAndroid)
    }

    @Test
    fun testLocalConfiguration() {
        check(offlineWeatherApp + mockAndroid)
    }

    @Test
    fun testTestConfiguration() {
        check(testWeatherApp + mockAndroid)
    }
}