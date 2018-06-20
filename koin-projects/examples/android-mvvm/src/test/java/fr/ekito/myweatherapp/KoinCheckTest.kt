package fr.ekito.myweatherapp

import android.app.Application
import fr.ekito.myweatherapp.di.offlineWeatherApp
import fr.ekito.myweatherapp.di.onlineWeatherApp
import fr.ekito.myweatherapp.di.testWeatherApp
import org.junit.Test
import org.koin.android.ext.koin.with
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.check
import org.mockito.Mockito.mock

/**
 * Test Koin modules
 */
class KoinCheckTest : AutoCloseKoinTest() {

    @Test
    fun testRemoteConfiguration() {
        startKoin(onlineWeatherApp) with mock(Application::class.java)
        check()
    }

    @Test
    fun testLocalConfiguration() {
        startKoin(offlineWeatherApp) with mock(Application::class.java)
        check()
    }

    @Test
    fun testTestConfiguration() {
        startKoin(testWeatherApp) with mock(Application::class.java)
        check()
    }
}