package fr.ekito.myweatherapp.mock.mvvm

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import fr.ekito.myweatherapp.domain.entity.DailyForecast
import fr.ekito.myweatherapp.domain.repository.WeatherRepository
import fr.ekito.myweatherapp.util.MockitoHelper.argumentCaptor
import fr.ekito.myweatherapp.util.TestSchedulerProvider
import fr.ekito.myweatherapp.view.Event
import fr.ekito.myweatherapp.view.FailedEvent
import fr.ekito.myweatherapp.view.LoadingEvent
import fr.ekito.myweatherapp.view.SuccessEvent
import fr.ekito.myweatherapp.view.splash.SplashViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class SplashViewModelMockTest {

    lateinit var viewModel: SplashViewModel
    @Mock
    lateinit var view: Observer<Event>
    @Mock
    lateinit var repository: WeatherRepository

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)

        viewModel = SplashViewModel(repository, TestSchedulerProvider())
        viewModel.events.observeForever(view)
    }

    @Test
    fun testGetLastWeather() = runBlocking {
        val list = listOf(mock(DailyForecast::class.java))

        given(repository.getWeather()).willReturn(async { list })

        viewModel.getLastWeather()

        // setup ArgumentCaptor
        val arg = argumentCaptor<Event>()
        // Here we expect 2 calls on view.onChanged
        verify(view, times(2)).onChanged(arg.capture())

        val values = arg.allValues
        // Test obtained values in order
        Assert.assertEquals(2, values.size)
        Assert.assertEquals(LoadingEvent, values[0])
        Assert.assertEquals(SuccessEvent, values[1])
    }

    @Test
    fun testGetLasttWeatherFailed() = runBlocking {
        val error = Throwable("Got an error")
        given(repository.getWeather()).will { throw error }

        viewModel.getLastWeather()

        // setup ArgumentCaptor
        val arg = argumentCaptor<Event>()
        // Here we expect 2 calls on view.onChanged
        verify(view, times(2)).onChanged(arg.capture())

        val values = arg.allValues
        // Test obtained values in order
        Assert.assertEquals(2, values.size)
        Assert.assertEquals(LoadingEvent, values[0])
        Assert.assertEquals(FailedEvent(error), values[1])
    }
}