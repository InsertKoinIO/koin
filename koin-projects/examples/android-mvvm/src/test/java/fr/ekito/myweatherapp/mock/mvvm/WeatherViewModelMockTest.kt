package fr.ekito.myweatherapp.mock.mvvm

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import fr.ekito.myweatherapp.data.repository.WeatherRepository
import fr.ekito.myweatherapp.mock.MockedData
import fr.ekito.myweatherapp.mock.MockedData.mockList
import fr.ekito.myweatherapp.util.MockitoHelper
import fr.ekito.myweatherapp.util.MockitoHelper.argumentCaptor
import fr.ekito.myweatherapp.util.TestSchedulerProvider
import fr.ekito.myweatherapp.view.ErrorState
import fr.ekito.myweatherapp.view.LoadingState
import fr.ekito.myweatherapp.view.ViewModelEvent
import fr.ekito.myweatherapp.view.ViewModelState
import fr.ekito.myweatherapp.view.weather.WeatherViewModel
import io.reactivex.Single
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class WeatherViewModelMockTest {

    lateinit var viewModel: WeatherViewModel
    @Mock
    lateinit var statesView: Observer<ViewModelState>
    @Mock
    lateinit var eventsView: Observer<ViewModelEvent>
    @Mock
    lateinit var repository: WeatherRepository

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)

        viewModel =
                WeatherViewModel(repository, TestSchedulerProvider())
        viewModel.states.observeForever(statesView)
        viewModel.events.observeForever(eventsView)
    }

    @Test
    fun testDisplayList() {
        val first = mockList.first()
        given(repository.getWeather()).willReturn(Single.just(mockList))

        viewModel.getWeather()

        // setup ArgumentCaptor
        val arg = argumentCaptor<ViewModelState>()
        // Here we expect 2 calls on statesView.onChanged
        verify(statesView, Mockito.times(2)).onChanged(arg.capture())

        val values = arg.allValues
        // Test obtained values in order
        Assert.assertEquals(2, values.size)
        Assert.assertEquals(LoadingState, values[0])
        Assert.assertEquals(
            WeatherViewModel.WeatherListState(
                MockedData.location,
                first,
                mockList.takeLast(mockList.size - 1)
            ), values[1]
        )
    }

    @Test
    fun testDisplayListFailed() {
        val error = Throwable("Got an error")
        given(repository.getWeather(MockitoHelper.any())).willReturn(Single.error(error))

        viewModel.getWeather()

        // setup ArgumentCaptor
        val arg = argumentCaptor<ViewModelState>()
        // Here we expect 2 calls on statesView.onChanged
        verify(statesView, Mockito.times(2)).onChanged(arg.capture())

        val values = arg.allValues
        // Test obtained values in order
        Assert.assertEquals(2, values.size)
        Assert.assertEquals(LoadingState, values[0])
        Assert.assertEquals(ErrorState(error), values[1])
    }

    @Test
    fun testSearchNewLocation() {
        val location = MockedData.location
        val first = mockList.first()
        given(repository.getWeather(location)).willReturn(Single.just(mockList))
        viewModel.loadNewLocation(location)

        // setup ArgumentCaptor
        val statesArg = argumentCaptor<ViewModelState>()
        // Here we expect 2 calls on statesView.onChanged
        verify(statesView, Mockito.times(1)).onChanged(statesArg.capture())

        // setup ArgumentCaptor
        val eventsArg = argumentCaptor<ViewModelEvent>()
        // Here we expect 2 calls on statesView.onChanged
        verify(eventsView, Mockito.times(1)).onChanged(eventsArg.capture())

        val statesValues = statesArg.allValues
        // Test obtained statesValues in order
        Assert.assertEquals(1, statesValues.size)
        Assert.assertEquals(
            WeatherViewModel.WeatherListState(
                location,
                first,
                mockList.takeLast(mockList.size - 1)
            ), statesValues[0]
        )

        val eventsValues = eventsArg.allValues
        // Test obtained statesValues in order
        Assert.assertEquals(1, eventsValues.size)
        Assert.assertEquals(WeatherViewModel.LoadingLocationEvent(location), eventsValues[0])
    }

    @Test
    fun testSearchNewLocationFailed() {
        val location = "new location"
        val error = Throwable("Got an error")
        given(repository.getWeather(location)).willReturn(Single.error(error))
        viewModel.loadNewLocation(location)

        // setup ArgumentCaptor
        val statesArg = argumentCaptor<ViewModelState>()
        // Here we expect 2 calls on statesView.onChanged
        verify(statesView, Mockito.times(0)).onChanged(statesArg.capture())

        // setup ArgumentCaptor
        val eventsArg = argumentCaptor<ViewModelEvent>()
        // Here we expect 2 calls on statesView.onChanged
        verify(eventsView, Mockito.times(2)).onChanged(eventsArg.capture())

        val statesValues = statesArg.allValues
        // Test obtained statesValues in order
        Assert.assertEquals(0, statesValues.size)

        val eventsValues = eventsArg.allValues
        // Test obtained statesValues in order
        Assert.assertEquals(2, eventsValues.size)
        Assert.assertEquals(WeatherViewModel.LoadingLocationEvent(location), eventsValues[0])
        Assert.assertEquals(
            WeatherViewModel.LoadLocationFailedEvent(location, error),
            eventsValues[1]
        )
    }

}