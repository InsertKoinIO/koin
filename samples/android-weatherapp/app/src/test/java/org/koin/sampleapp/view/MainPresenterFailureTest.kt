package org.koin.sampleapp.view

import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.koin.Koin
import org.koin.log.PrintLogger
import org.koin.sampleapp.repository.WeatherRepository
import org.koin.sampleapp.util.TestSchedulerProvider
import org.koin.sampleapp.util.any
import org.koin.sampleapp.view.main.MainContract
import org.koin.sampleapp.view.main.MainPresenter
import org.koin.test.KoinTest
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class MainPresenterFailureTest : KoinTest {

    lateinit var presenter: MainContract.Presenter
    @Mock lateinit var view: MainContract.View
    @Mock lateinit var repository: WeatherRepository

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)
        presenter = MainPresenter(repository, TestSchedulerProvider())

        presenter.view = view
    }

    @Test
    fun testGetWeather() {
        val locationString = "Paris, france"

        `when`(repository.getWeather(ArgumentMatchers.anyString())).thenReturn(Single.error(IllegalStateException("Go an error")))

        presenter.getWeather(locationString)

        Mockito.verify(view).displayNormal()
        Mockito.verify(view).onWeatherFailed(any())
    }
}