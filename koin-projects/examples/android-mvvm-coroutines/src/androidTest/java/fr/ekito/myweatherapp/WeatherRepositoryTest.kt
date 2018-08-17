package fr.ekito.myweatherapp

import android.support.test.runner.AndroidJUnit4
import fr.ekito.myweatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.experimental.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest

/**
 * WeatherRepositoryTest is a KoinTest with AndroidJUnit4 runner
 *
 * KoinTest help inject Koin components from actual runtime
 */
@RunWith(AndroidJUnit4::class)
class WeatherRepositoryTest : KoinTest {

    /*
     * Inject WeatherRepository from Koin
     */
    val weatherRepository: WeatherRepository by inject()

    @Before()
    fun before() {
        loadKoinModules(roomTestModule)
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun testGetDefault() = runBlocking {
        val defaultWeather = weatherRepository.getWeather().await()
        val defaultWeather2 = weatherRepository.getWeather().await()
        Assert.assertEquals(defaultWeather, defaultWeather2)
    }

    @Test
    fun testGetWeatherDetail() = runBlocking {
        val defaultWeather = weatherRepository.getWeather().await()

        val result = defaultWeather.first()
        val first = weatherRepository.getWeatherDetail(result.id).await()
        Assert.assertEquals(result, first)
    }

    @Test
    fun testGetLatest() = runBlocking {
        weatherRepository.getWeather().await()
        weatherRepository.getWeather("London").await()
        val toulouse = weatherRepository.getWeather("Toulouse").await()
        val defaultWeather3 = weatherRepository.getWeather().await()
        Assert.assertEquals(defaultWeather3, toulouse)
    }
}