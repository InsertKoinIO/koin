//package fr.ekito.myweatherapp.mock.mvvm
//
//import android.arch.core.executor.testing.InstantTaskExecutorRule
//import android.arch.lifecycle.Observer
//import fr.ekito.myweatherapp.domain.repository.DailyForecastRepository
//import fr.ekito.myweatherapp.mock.MockedData.mockList
//import fr.ekito.myweatherapp.util.TestSchedulerProvider
//import fr.ekito.myweatherapp.view.Failed
//import fr.ekito.myweatherapp.view.Loading
//import fr.ekito.myweatherapp.view.ViewModelState
//import fr.ekito.myweatherapp.view.weather.WeatherViewModel
//import io.reactivex.Single
//import org.junit.Assert
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.mockito.ArgumentCaptor
//import org.mockito.BDDMockito.given
//import org.mockito.Mock
//import org.mockito.Mockito
//import org.mockito.Mockito.verify
//import org.mockito.MockitoAnnotations
//
//class WeatherListViewModelMockTest {
//
//    lateinit var viewModel: WeatherViewModel
//    @Mock
//    lateinit var view: Observer<ViewModelState>
//    @Mock
//    lateinit var repository: DailyForecastRepository
//
//    @get:Rule
//    val rule = InstantTaskExecutorRule()
//
//    @Before
//    fun before() {
//        MockitoAnnotations.initMocks(this)
//
//        viewModel = WeatherViewModel(repository, TestSchedulerProvider())
//        viewModel.states.observeForever(view)
//    }
//
//    @Test
//    fun testDisplayList() {
//        given(repository.getWeather()).willReturn(Single.just(mockList))
//
//        viewModel.getWeather()
//
//        // setup ArgumentCaptor
//        val arg = ArgumentCaptor.forClass(ViewModelState::class.java)
//        // Here we expect 2 calls on view.onChanged
//        verify(view, Mockito.times(2)).onChanged(arg.capture())
//
//        val states = arg.allValues
//        // Test obtained values in order
//        Assert.assertEquals(2, states.size)
//        Assert.assertEquals(Loading, states[0])
//        Assert.assertEquals(WeatherViewModel.WeatherListLoaded.from(mockList), states[1])
//    }
//
//    @Test
//    fun testDisplayListFailed() {
//        val error = Throwable("Got an error")
//        given(repository.getWeather()).willReturn(Single.error(error))
//
//        viewModel.getWeather()
//
//        // setup ArgumentCaptor
//        val arg = ArgumentCaptor.forClass(ViewModelState::class.java)
//        // Here we expect 2 calls on view.onChanged
//        verify(view, Mockito.times(2)).onChanged(arg.capture())
//
//        val states = arg.allValues
//        // Test obtained values in order
//        Assert.assertEquals(2, states.size)
//        Assert.assertEquals(Loading, states[0])
//        Assert.assertEquals(Failed(error), states[1])
//    }
//
//}