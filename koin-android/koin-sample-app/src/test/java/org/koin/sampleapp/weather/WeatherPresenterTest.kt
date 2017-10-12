package org.koin.sampleapp.weather

import junit.framework.Assert
import org.junit.Before
import org.junit.Test
import org.koin.Koin
import org.koin.sampleapp.di.RxTestModule
import org.koin.sampleapp.di.WeatherModule
import org.koin.sampleapp.di.testRemoteDatasource
import org.koin.sampleapp.repository.WeatherDatasource
import org.koin.sampleapp.util.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class WeatherPresenterTest {

    lateinit var presenter: WeatherContract.Presenter
    @Mock lateinit var view: WeatherContract.View
    @Mock lateinit var weatherWS: WeatherDatasource

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)
        val context = Koin().build(testRemoteDatasource())
        // inject server property
        context.setProperty(WeatherModule.SERVER_URL, RxTestModule.SERVER_URL)

        presenter = context.get()
        presenter.view = view
    }

    @Test
    fun testDisplayWeather() {
        Assert.assertNotNull(presenter)

        val locationString = "Paris, france"
        presenter.getWeather(locationString)

        Mockito.verify(view).displayWeather(any(), any())
    }

}