package fr.ekito.myweatherapp

import android.app.Application
import fr.ekito.myweatherapp.di.offlineWeatherApp
import fr.ekito.myweatherapp.di.onlineWeatherApp
import fr.ekito.myweatherapp.di.testWeatherApp
import org.junit.After
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.test.KoinTest
import org.koin.test.check
import org.mockito.Mockito.mock

/**
 * Test Koin modules
 */
class DryRunTest : KoinTest {

    @After
    fun after() {
        closeKoin()
    }

    @Test
    fun testRemoteConfiguration() {
        check(onlineWeatherApp + module {
            single { mock(Application::class.java) }
        })
    }

    @Test
    fun testLocalConfiguration() {
        check(offlineWeatherApp + module {
            single { mock(Application::class.java) }
        })
    }

    @Test
    fun testTestConfiguration() {
        check(testWeatherApp + module {
            single { mock(Application::class.java) }
        })
    }
}