package org.koin.sampleapp.datasource

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.sampleapp.di.testLocalDatasource
import org.koin.sampleapp.repository.WeatherRepositoryImpl
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest

class RepositoryTest : KoinTest {

    val repository by inject<WeatherRepositoryImpl>()

    @Before
    fun before() {
        startKoin(testLocalDatasource)
    }

    @After
    fun after() {
        closeKoin()
    }

    @Test
    fun testGetWeather() {
        val weather1 = repository.getWeather("Paris").blockingGet()
        val weather2 = repository.getWeather("Paris").blockingGet()

        assertEquals(weather1, weather2)
        assertEquals(weather1,repository.weatherCache)
        assertEquals(weather2,repository.weatherCache)
    }
}