package fr.ekito.myweatherapp

import fr.ekito.myweatherapp.data.local.JsonReader
import fr.ekito.myweatherapp.di.Properties
import fr.ekito.myweatherapp.di.offlineWeatherApp
import fr.ekito.myweatherapp.di.onlineWeatherApp
import fr.ekito.myweatherapp.di.testWeatherApp
import fr.ekito.myweatherapp.view.detail.DetailContract
import org.junit.Test
import org.koin.core.logger.Level
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import org.koin.test.check.parameterCreatorsOf
import org.mockito.Mockito.mock

/**
 * Test Koin modules
 */
class ModuleCheckTest : KoinTest {

    private val mocks = module(override = true) {
        single<JsonReader> { mock(JsonReader::class.java) }
    }
    private val mockProperties = mapOf(
            Properties.SERVER_URL to "http://mock/server/url/"
    )
    private val parameterCreators = parameterCreatorsOf {
        create<DetailContract.Presenter> { parametersOf("id") }
    }

    @Test
    fun testRemoteConfiguration() {
        koinApplication {
            logger(Level.DEBUG)
            modules(onlineWeatherApp + mocks)
            properties(mockProperties)
        }.checkModules(parameterCreators)
    }

    @Test
    fun testLocalConfiguration() {
        koinApplication {
            logger(Level.DEBUG)
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
}