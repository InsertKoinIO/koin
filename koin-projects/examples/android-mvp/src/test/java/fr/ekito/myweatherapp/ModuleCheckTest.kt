package fr.ekito.myweatherapp

import android.app.Application
import android.content.Context
import fr.ekito.myweatherapp.di.offlineWeatherApp
import fr.ekito.myweatherapp.di.onlineWeatherApp
import fr.ekito.myweatherapp.di.testWeatherApp
import org.junit.After
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.test.KoinTest
import org.koin.test.checkModules
import org.mockito.Mockito.mock

/**
 * Test Koin modules
 */
class ModuleCheckTest : KoinTest {

    val mockedAndroidContext = module {
        single { mock(Application::class.java) }
    }

    @After
    fun after() {
    }

    @Test
    fun testRemoteConfiguration() {
        checkModules(onlineWeatherApp)
    }

    @Test
    fun testLocalConfiguration() {
        checkModules(offlineWeatherApp + mockedAndroidContext)
    }

    @Test
    fun testTestConfiguration() {
        checkModules(testWeatherApp + mockedAndroidContext)
    }
}