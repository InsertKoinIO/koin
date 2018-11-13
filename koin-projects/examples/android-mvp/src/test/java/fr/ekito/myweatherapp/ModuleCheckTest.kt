package fr.ekito.myweatherapp

import android.app.Application
import fr.ekito.myweatherapp.di.offlineWeatherApp
import fr.ekito.myweatherapp.di.onlineWeatherApp
import fr.ekito.myweatherapp.di.testWeatherApp
import org.junit.After
import org.junit.Test
import org.koin.android.ext.koin.useAndroidContext
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

    @After
    fun after() {
    }

    @Test
    fun testRemoteConfiguration() {
        koinApplication {
            useLogger(Level.DEBUG)
            loadModules(onlineWeatherApp)
        }.checkModules()
    }

    @Test
    fun testLocalConfiguration() {
        koinApplication {
            useLogger(Level.DEBUG)
            useAndroidContext(mockedApplication)
            loadModules(offlineWeatherApp)
        }.checkModules()
    }

    @Test
    fun testTestConfiguration() {
        koinApplication {
            useLogger(Level.DEBUG)
            useAndroidContext(mockedApplication)
            loadModules(testWeatherApp)
        }.checkModules()
    }
}