package fr.ekito.myweatherapp

import android.support.test.runner.AndroidJUnit4
import fr.ekito.myweatherapp.domain.repository.DailyForecastRepository
import junit.framework.Assert
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest

@RunWith(AndroidJUnit4::class)
class WeatherRepositoryTest : KoinTest {

    private val weatherRepository: DailyForecastRepository by inject()

    @Before()
    fun before() {
        loadKoinModules(roomTestModule)
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun testGetDefault() {
        val defaultWeather = weatherRepository.getWeather().blockingGet()
        val defaultWeather2 = weatherRepository.getWeather().blockingGet()
        Assert.assertEquals(defaultWeather, defaultWeather2)
    }

    @Test
    fun testGetWeatherDetail() {
        val defaultWeather = weatherRepository.getWeather().blockingGet()

        val result = defaultWeather.first()
        val first = weatherRepository.getWeatherDetail(result.id).blockingGet()
        Assert.assertEquals(result, first)
    }

    @Test
    fun testGetLatest() {
        weatherRepository.getWeather().blockingGet()
        weatherRepository.getWeather("London").blockingGet()
        val toulouse = weatherRepository.getWeather("Toulouse").blockingGet()
        val defaultWeather3 = weatherRepository.getWeather().blockingGet()
        Assert.assertEquals(defaultWeather3, toulouse)
    }
}