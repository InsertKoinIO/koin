package fr.ekito.myweatherapp

import android.app.Application
import fr.ekito.myweatherapp.data.local.JsonReader
import fr.ekito.myweatherapp.di.*
import fr.ekito.myweatherapp.view.detail.DetailViewModel
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.core.logger.Level
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.check.ParametersCreator
import org.koin.test.check.checkModules
import org.mockito.Mockito.mock
import kotlin.reflect.KClass

/**
 * Test Koin modules
 */
class ModuleCheckTest : KoinTest {

    private val mockApplication = mock(Application::class.java)
    private val mocks = module(override = true) {
        single<JsonReader> { mock(JsonReader::class.java) }
    }
    private val mockProperties = mapOf(
            Properties.SERVER_URL to "http://mock/server/url/"
    )
    private val parameterCreators = mapOf<KClass<*>, ParametersCreator>(
            DetailViewModel::class to { parametersOf("id") }
    )

    @Test
    fun testRemoteConfiguration() {
        koinApplication {
            modules(onlineWeatherApp + mocks)
            properties(mockProperties)
        }.checkModules(parameterCreators)
    }

    @Test
    fun testLocalConfiguration() {
        koinApplication {
            modules(offlineWeatherApp + mocks)
        }.checkModules(parameterCreators)
    }

    @Test
    fun testTestConfiguration() {
        koinApplication {
            logger(Level.DEBUG)
            modules(testWeatherApp + mocks)
        }.checkModules(parameterCreators)
    }

    @Test
    fun testRoomConfiguration() {
        koinApplication {
            androidContext(mockApplication)
            modules(roomWeatherApp + mocks)
        }.checkModules(parameterCreators)
    }
}