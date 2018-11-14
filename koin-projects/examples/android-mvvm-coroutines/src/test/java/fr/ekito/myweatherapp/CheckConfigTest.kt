package fr.ekito.myweatherapp

import android.app.Application
import fr.ekito.myweatherapp.di.offlineWeatherApp
import fr.ekito.myweatherapp.di.onlineWeatherApp
import fr.ekito.myweatherapp.di.testWeatherApp
import org.junit.Test
import org.koin.android.ext.koin.useAndroidContext
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import org.mockito.Mockito.mock

/**
 * Test Koin modules
 */
class CheckConfigTest : KoinTest {

    val mockedAndroidContext = mock(Application::class.java)

    @Test
    fun testRemoteConfiguration() {
        koinApplication {
            useAndroidContext(mockedAndroidContext)
            loadModules(onlineWeatherApp)
        }.checkModules()
    }

    @Test
    fun testLocalConfiguration() {
        koinApplication {
            useAndroidContext(mockedAndroidContext)
            loadModules(offlineWeatherApp)
        }.checkModules()
    }

    @Test
    fun testTestConfiguration() {
        koinApplication {
            useAndroidContext(mockedAndroidContext)
            loadModules(testWeatherApp)
        }.checkModules()
    }
}