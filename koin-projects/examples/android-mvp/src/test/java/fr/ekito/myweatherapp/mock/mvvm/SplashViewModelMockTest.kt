//package fr.ekito.myweatherapp.mock.mvvm
//
//import android.arch.core.executor.testing.InstantTaskExecutorRule
//import android.arch.lifecycle.Observer
//import fr.ekito.myweatherapp.domain.entity.DailyForecast
//import fr.ekito.myweatherapp.domain.repository.DailyForecastRepository
//import fr.ekito.myweatherapp.util.TestSchedulerProvider
//import fr.ekito.myweatherapp.view.Fail
//import fr.ekito.myweatherapp.view.Pending
//import fr.ekito.myweatherapp.view.Success
//import fr.ekito.myweatherapp.view.ViewModelEvent
//import fr.ekito.myweatherapp.view.splash.SplashViewModel
//import io.reactivex.Single
//import org.junit.Assert
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.mockito.ArgumentCaptor
//import org.mockito.BDDMockito.given
//import org.mockito.Mock
//import org.mockito.Mockito.*
//import org.mockito.MockitoAnnotations
//
//class SplashViewModelMockTest {
//
//    lateinit var viewModel: SplashViewModel
//
//    @Mock
//    lateinit var view: Observer<ViewModelEvent>
//
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
//        viewModel = SplashViewModel(repository, TestSchedulerProvider())
//
//        viewModel.events.observeForever(view)
//    }
//
//    @Test
//    fun testGetLastWeather() {
//        val list = listOf(mock(DailyForecast::class.java))
//
//        given(repository.getWeather()).willReturn(Single.just(list))
//
//        viewModel.getLastWeather()
//
//        // setup ArgumentCaptor
//        val arg = ArgumentCaptor.forClass(ViewModelEvent::class.java)
//        // Here we expect 2 calls on view.onChanged
//        verify(view, times(2)).onChanged(arg.capture())
//
//        val values = arg.allValues
//        // Test obtained values in order
//        Assert.assertEquals(2, values.size)
//        Assert.assertEquals(Pending, values[0])
//        Assert.assertEquals(Success, values[1])
//    }
//
//    @Test
//    fun testGetLasttWeatherFailed() {
//        val error = Throwable("Got an error")
//        given(repository.getWeather()).willReturn(Single.error(error))
//
//        viewModel.getLastWeather()
//
//        // setup ArgumentCaptor
//        val arg = ArgumentCaptor.forClass(ViewModelEvent::class.java)
//        // Here we expect 2 calls on view.onChanged
//        verify(view, times(2)).onChanged(arg.capture())
//
//        val values = arg.allValues
//        // Test obtained values in order
//        Assert.assertEquals(2, values.size)
//        Assert.assertEquals(Pending, values[0])
//        Assert.assertEquals(Fail(error), values[1])
//    }
//}