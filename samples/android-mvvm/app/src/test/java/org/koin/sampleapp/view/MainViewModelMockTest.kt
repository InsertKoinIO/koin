package org.koin.sampleapp.view

import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.koin.sampleapp.repository.WeatherRepository
import org.koin.sampleapp.repository.json.weather.Weather
import org.koin.sampleapp.view.main.MainViewModel
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class MainViewModelMockTest {

    lateinit var mainViewModel: MainViewModel
    @Mock lateinit var repository: WeatherRepository

        val locationString = "Paris, france"
    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)
        mainViewModel = MainViewModel(repository)
    }

    @Test
    fun testGetWeather() {
        val mock = mock(Weather::class.java)
        `when`(repository.getWeather(ArgumentMatchers.anyString())).thenReturn(Single.just(mock))

        val test = mainViewModel.searchWeather(locationString).test()

        test.assertComplete()
    }

    @Test
    fun testGetWeatherFail() {
        `when`(repository.getWeather(ArgumentMatchers.anyString())).thenReturn(Single.error(IllegalStateException("Boom")))

        val test = mainViewModel.searchWeather(locationString).test()

        test.assertError { e -> e is IllegalStateException }
    }
}