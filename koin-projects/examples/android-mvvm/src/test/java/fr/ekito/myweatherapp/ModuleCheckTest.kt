package fr.ekito.myweatherapp

import android.app.Application
import fr.ekito.myweatherapp.di.offlineWeatherApp
import fr.ekito.myweatherapp.di.onlineWeatherApp
import fr.ekito.myweatherapp.di.roomWeatherApp
import fr.ekito.myweatherapp.di.testWeatherApp
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import org.mockito.Mockito.mock

/**
 * Test Koin modules
 */
class ModuleCheckTest : KoinTest {

    val mockedAndroidContext = mock(Application::class.java)

    @Test
    fun testRemoteConfiguration() {
        koinApplication {
            androidContext(mockedAndroidContext)
            modules(onlineWeatherApp)
        }.checkModules()
    }

    @Test
    fun testLocalConfiguration() {
        koinApplication {
            androidContext(mockedAndroidContext)
            modules(offlineWeatherApp)
        }.checkModules()
    }

    @Test
    fun testTestConfiguration() {
        koinApplication {
            androidContext(mockedAndroidContext)
            modules(testWeatherApp)
        }.checkModules()
    }

    @Test
    fun testRoomConfiguration() {
        koinApplication {
            androidContext(mockedAndroidContext)
            modules(roomWeatherApp)
        }.checkModules()
    }
}