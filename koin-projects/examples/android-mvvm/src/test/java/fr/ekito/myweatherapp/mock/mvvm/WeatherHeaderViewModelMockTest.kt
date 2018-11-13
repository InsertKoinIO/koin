package fr.ekito.myweatherapp.mock.mvvm

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import fr.ekito.myweatherapp.domain.repository.DailyForecastRepository
import fr.ekito.myweatherapp.mock.MockedData.mockList
import fr.ekito.myweatherapp.util.MockitoHelper
import fr.ekito.myweatherapp.util.TestSchedulerProvider
import fr.ekito.myweatherapp.view.Failed
import fr.ekito.myweatherapp.view.Loading
import fr.ekito.myweatherapp.view.ViewModelEvent
import fr.ekito.myweatherapp.view.ViewModelState
import fr.ekito.myweatherapp.view.weather.WeatherViewModel
import io.reactivex.Single
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class WeatherHeaderViewModelMockTest {

    lateinit var viewModel: WeatherViewModel

    @Mock
    lateinit var viewStates: Observer<ViewModelState>

    @Mock
    lateinit var viewEvents: Observer<ViewModelEvent>

    @Mock
    lateinit var repository: DailyForecastRepository

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)

        viewModel = WeatherViewModel(repository, TestSchedulerProvider())
        viewModel.events.observeForever(viewEvents)
        viewModel.states.observeForever(viewStates)
    }

    @Test
    fun testDisplayList() {
        given(repository.getWeather()).willReturn(Single.just(mockList))

        viewModel.getWeather()

        // setup ArgumentCaptor
        val arg = ArgumentCaptor.forClass(ViewModelState::class.java)
        // Here we expect 2 calls on view.onChanged
        verify(viewStates, Mockito.times(2)).onChanged(arg.capture())

        val states = arg.allValues
        // Test obtained values in order
        Assert.assertEquals(2, states.size)
        Assert.assertEquals(Loading, states[0])
        Assert.assertEquals(WeatherViewModel.WeatherListLoaded.from(mockList), states[1])
    }

    @Test
    fun testDisplayListFailed() {
        val error = Throwable("Got an error")
        given(repository.getWeather(MockitoHelper.any())).willReturn(Single.error(error))

        viewModel.getWeather()

        // setup ArgumentCaptor
        val arg = ArgumentCaptor.forClass(ViewModelState::class.java)
        // Here we expect 2 calls on view.onChanged
        verify(viewStates, Mockito.times(2)).onChanged(arg.capture())

        val values = arg.allValues
        // Test obtained values in order
        Assert.assertEquals(2, values.size)
        Assert.assertEquals(Loading, values[0])
        Assert.assertEquals(Failed(error), values[1])
    }

    @Test
    fun testSearchNewLocation() {
        val location = "new location"
        given(repository.getWeather(location)).willReturn(Single.just(mockList))
        viewModel.loadNewLocation(location)

        // setup ArgumentCaptor
        val argStates = ArgumentCaptor.forClass(ViewModelState::class.java)
        // Here we expect 2 calls on view.onChanged
        verify(viewStates, Mockito.times(1)).onChanged(argStates.capture())

        val states = argStates.allValues
        // Test obtained values in order
        Assert.assertEquals(1, states.size)
        Assert.assertEquals(WeatherViewModel.WeatherListLoaded.from(mockList), states[0])

        // setup ArgumentCaptor
        val argEvents = ArgumentCaptor.forClass(ViewModelEvent::class.java)
        // Here we expect 2 calls on view.onChanged
        verify(viewEvents, Mockito.times(1)).onChanged(argEvents.capture())

        val events = argEvents.allValues
        // Test obtained values in order
        Assert.assertEquals(1, events.size)
        Assert.assertEquals(WeatherViewModel.ProceedLocation(location), events[0])
    }

    @Test
    fun testSearchNewLocationFailed() {
        val location = "new location"
        val error = Throwable("Got an error")

        given(repository.getWeather(location)).willReturn(Single.error(error))
        viewModel.loadNewLocation(location)

        // setup ArgumentCaptor
        val argStates = ArgumentCaptor.forClass(ViewModelState::class.java)
        // Here we expect 2 calls on view.onChanged
        verify(viewStates, Mockito.times(0)).onChanged(argStates.capture())

        val states = argStates.allValues
        // Test obtained values in order
        Assert.assertEquals(0, states.size)

        // setup ArgumentCaptor
        val argEvents = ArgumentCaptor.forClass(ViewModelEvent::class.java)
        // Here we expect 2 calls on view.onChanged
        verify(viewEvents, Mockito.times(2)).onChanged(argEvents.capture())

        val events = argEvents.allValues
        // Test obtained values in order
        Assert.assertEquals(2, events.size)
        Assert.assertEquals(WeatherViewModel.ProceedLocation(location), events[0])
        Assert.assertEquals(WeatherViewModel.ProceedLocationError(location, error), events[1])
    }

}