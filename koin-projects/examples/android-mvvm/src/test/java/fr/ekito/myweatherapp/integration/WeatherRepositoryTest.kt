package fr.ekito.myweatherapp.integration

import fr.ekito.myweatherapp.di.testWeatherApp
import fr.ekito.myweatherapp.domain.repository.DailyForecastRepository
import junit.framework.Assert
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest

class WeatherRepositoryTest : KoinTest {

    val repository by inject<DailyForecastRepository>()

    val location = "Paris"

    @Before
    fun before() {
        startKoin(testWeatherApp)
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun testGetWeatherSuccess() {
        repository.getWeather(location).blockingGet()
        val test = repository.getWeather(location).test()
        test.awaitTerminalEvent()
        test.assertComplete()
    }

    @Test
    fun testCachedWeather() {
        val l1 = repository.getWeather("Paris").blockingGet()
        val l2 = repository.getWeather("Toulouse").blockingGet()
        val l3 = repository.getWeather().blockingGet()

        Assert.assertEquals(l3, l2)
        Assert.assertNotSame(l1, l2)
    }

    @Test
    fun testGetDetail() {
        repository.getWeather(location).blockingGet()
        val list = repository.getWeather(location).blockingGet()
        val first = list.first()
        val test = repository.getWeatherDetail(first.id).test()
        test.awaitTerminalEvent()
        test.assertValue { it == first }
    }
}