package fr.ekito.myweatherapp.weather

import fr.ekito.myweatherapp.di.TestWebServicesModule
import fr.ekito.myweatherapp.util.any
import junit.framework.Assert
import koin.sampleapp.di.WeatherModule
import koin.sampleapp.di.WebModule
import koin.sampleapp.service.WeatherWS
import koin.sampleapp.weather.WeatherContract
import org.junit.Before
import org.junit.Test
import org.koin.Koin
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class WeatherPresenterTest {

    lateinit var presenter: WeatherContract.Presenter
    @Mock lateinit var view: WeatherContract.View
    @Mock lateinit var weatherWS: WeatherWS

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)
        val context = Koin().build(arrayListOf(TestWebServicesModule(), WebModule(), WeatherModule()))
        // provide mock as View component
        context.provide { view }
        // inject server property
        context.setProperty(WebModule.SERVER_URL, TestWebServicesModule.SERVER_URL)

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