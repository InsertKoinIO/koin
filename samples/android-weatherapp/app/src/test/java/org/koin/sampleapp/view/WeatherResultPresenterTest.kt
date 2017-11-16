package org.koin.sampleapp.view

import org.junit.Before
import org.junit.Test
import org.koin.Koin
import org.koin.log.PrintLogger
import org.koin.sampleapp.di.testLocalDatasource
import org.koin.sampleapp.util.any
import org.koin.sampleapp.view.weather.WeatherResultContract
import org.koin.standalone.inject
import org.koin.standalone.startContext
import org.koin.test.KoinTest
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class WeatherResultPresenterTest : KoinTest {

    val presenter by inject<WeatherResultContract.Presenter>()
    @Mock lateinit var view: WeatherResultContract.View


    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)
        Koin.logger = PrintLogger()
        startContext(testLocalDatasource())

        presenter.view = view
    }

    @Test
    fun testDisplayWeather() {
        val locationString = "Paris, france"
        presenter.getWeather(locationString)

        Mockito.verify(view).displayWeather(any())
    }

}