package fr.ekito.myweatherapp

import android.app.Application
import fr.ekito.myweatherapp.di.offlineWeatherApp
import fr.ekito.myweatherapp.di.onlineWeatherApp
import fr.ekito.myweatherapp.di.testWeatherApp
import org.junit.After
import org.junit.Test
import org.koin.android.ext.koin.with
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.test.KoinTest
import org.koin.test.check
import org.koin.test.dryRun
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
        startKoin(onlineWeatherApp) with mock(Application::class.java)
        check()
        dryRun()
    }

    @Test
    fun testLocalConfiguration() {
        startKoin(offlineWeatherApp) with mock(Application::class.java)
        check()
        dryRun()
    }

    @Test
    fun testTestConfiguration() {
        startKoin(testWeatherApp) with mock(Application::class.java)
        check()
        dryRun()
    }
}