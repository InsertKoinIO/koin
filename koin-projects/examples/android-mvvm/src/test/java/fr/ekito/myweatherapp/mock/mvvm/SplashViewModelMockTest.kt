package fr.ekito.myweatherapp.mock.mvvm

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import fr.ekito.myweatherapp.data.repository.WeatherRepository
import fr.ekito.myweatherapp.domain.DailyForecastModel
import fr.ekito.myweatherapp.util.MockitoHelper.argumentCaptor
import fr.ekito.myweatherapp.util.TestSchedulerProvider
import fr.ekito.myweatherapp.view.FailedEvent
import fr.ekito.myweatherapp.view.LoadingEvent
import fr.ekito.myweatherapp.view.SuccessEvent
import fr.ekito.myweatherapp.view.ViewModelEvent
import fr.ekito.myweatherapp.view.splash.SplashViewModel
import io.reactivex.Single
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
    lateinit var view: Observer<ViewModelEvent>
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
    fun testGetLastWeather() {
        val list = listOf(mock(DailyForecastModel::class.java))

        given(repository.getWeather()).willReturn(Single.just(list))

        viewModel.getLastWeather()

        // setup ArgumentCaptor
        val arg = argumentCaptor<ViewModelEvent>()
        // Here we expect 2 calls on view.onChanged
        verify(view, times(2)).onChanged(arg.capture())

        val values = arg.allValues
        // Test obtained values in order
        Assert.assertEquals(2, values.size)
        Assert.assertEquals(LoadingEvent, values[0])
        Assert.assertEquals(SuccessEvent, values[1])
    }

    @Test
    fun testGetLasttWeatherFailed() {
        val error = Throwable("Got an error")
        given(repository.getWeather()).willReturn(Single.error(error))

        viewModel.getLastWeather()

        // setup ArgumentCaptor
        val arg = argumentCaptor<ViewModelEvent>()
        // Here we expect 2 calls on view.onChanged
        verify(view, times(2)).onChanged(arg.capture())

        val values = arg.allValues
        // Test obtained values in order
        Assert.assertEquals(2, values.size)
        Assert.assertEquals(LoadingEvent, values[0])
        Assert.assertEquals(FailedEvent(error), values[1])
    }
}