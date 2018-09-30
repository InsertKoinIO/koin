//package fr.ekito.myweatherapp.mock.mvvm
//
//import android.arch.core.executor.testing.InstantTaskExecutorRule
//import android.arch.lifecycle.Observer
//import fr.ekito.myweatherapp.domain.entity.DailyForecast
//import fr.ekito.myweatherapp.domain.repository.DailyForecastRepository
//import fr.ekito.myweatherapp.util.TestSchedulerProvider
//import fr.ekito.myweatherapp.view.Failed
//import fr.ekito.myweatherapp.view.Loading
//import fr.ekito.myweatherapp.view.ViewModelState
//import fr.ekito.myweatherapp.view.detail.DetailViewModel
//import io.reactivex.Single
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.mockito.ArgumentCaptor
//import org.mockito.BDDMockito.given
//import org.mockito.Mock
//import org.mockito.Mockito
//import org.mockito.Mockito.times
//import org.mockito.Mockito.verify
//import org.mockito.MockitoAnnotations
//
//class DetailViewModelMockTest {
//
//    lateinit var detailViewModel: DetailViewModel
//    @Mock
//    lateinit var view: Observer<ViewModelState>
//    @Mock
//    lateinit var repository: DailyForecastRepository
//
//    @get:Rule
//    val rule = InstantTaskExecutorRule()
//
//    val id = "ID"
//
//    @Before
//    fun before() {
//        MockitoAnnotations.initMocks(this)
//
//        detailViewModel = DetailViewModel()
//        detailViewModel.schedulerProvider = TestSchedulerProvider()
//        detailViewModel.dailyForecastRepository = repository
//
//        detailViewModel.states.observeForever(view)
//    }
//
//    @Test
//    fun testGetLastWeather() {
//        val weather = Mockito.mock(DailyForecast::class.java)
//
//        given(repository.getWeatherDetail(id)).willReturn(Single.just(weather))
//
//        detailViewModel.getDetail(id)
//
//        // setup ArgumentCaptor
//        val arg = ArgumentCaptor.forClass(ViewModelState::class.java)
//        // Here we expect 2 calls on view.onChanged
//        verify(view, times(2)).onChanged(arg.capture())
//
//        val values = arg.allValues
//        // Test obtained values in order
//        assertEquals(2, values.size)
//        assertEquals(Loading, values[0])
//        assertEquals(DetailViewModel.DetailLoaded(weather), values[1])
//    }
//
//    @Test
//    fun testGeLasttWeatherFailed() {
//        val error = Throwable("Got error")
//
//        given(repository.getWeatherDetail(id)).willReturn(Single.error(error))
//
//        detailViewModel.getDetail(id)
//
//        // setup ArgumentCaptor
//        val arg = ArgumentCaptor.forClass(ViewModelState::class.java)
//        // Here we expect 2 calls on view.onChanged
//        verify(view, times(2)).onChanged(arg.capture())
//
//        val values = arg.allValues
//        // Test obtained values in order
//        assertEquals(2, values.size)
//        assertEquals(Loading, values[0])
//        assertEquals(Failed(error), values[1])
//    }
//}