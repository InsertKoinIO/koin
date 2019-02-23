package fr.ekito.myweatherapp

import android.app.Application
import fr.ekito.myweatherapp.di.offlineWeatherApp
import fr.ekito.myweatherapp.di.onlineWeatherApp
import fr.ekito.myweatherapp.di.testWeatherApp
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.core.logger.Level
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import org.mockito.Mockito.mock

/**
 * Test Koin modules
 */
class ModuleCheckTest : KoinTest {

    val mockedApplication = mock(Application::class.java)

    @Test
    fun testRemoteConfiguration() {
        koinApplication {
            defaultLogger(Level.DEBUG)
            modules(onlineWeatherApp)
        }.checkModules()
    }

    @Test
    fun testLocalConfiguration() {
        koinApplication {
            defaultLogger(Level.DEBUG)
            androidContext(mockedApplication)
            modules(offlineWeatherApp)
        }.checkModules()
    }

    @Test
    fun testTestConfiguration() {
        koinApplication {
            defaultLogger(Level.DEBUG)
            androidContext(mockedApplication)
            modules(testWeatherApp)
        }.checkModules()
    }
}