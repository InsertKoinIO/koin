//package fr.ekito.myweatherapp.mock.mvvm
//
//import android.arch.core.executor.testing.InstantTaskExecutorRule
//import android.arch.lifecycle.Observer
//import fr.ekito.myweatherapp.data.repository.WeatherRepository
//import fr.ekito.myweatherapp.domain.DailyForecastModel
//import fr.ekito.myweatherapp.util.MockitoHelper.argumentCaptor
//import fr.ekito.myweatherapp.util.TestSchedulerProvider
//import fr.ekito.myweatherapp.view.ErrorState
//import fr.ekito.myweatherapp.view.LoadingState
//import fr.ekito.myweatherapp.view.ViewModelState
//import fr.ekito.myweatherapp.view.detail.DetailViewModel
//import io.reactivex.Single
//import org.junit.Assert
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.mockito.BDDMockito.given
//import org.mockito.Mock
//import org.mockito.Mockito.*
//import org.mockito.MockitoAnnotations
//
//class DetailViewModelMockTest {
//
//    lateinit var viewModel: DetailViewModel
//    @Mock
//    lateinit var view: Observer<ViewModelState>
//    @Mock
//    lateinit var repository: WeatherRepository
//
//    @get:Rule
//    val rule = InstantTaskExecutorRule()
//
//    @Before
//    fun before() {
//        MockitoAnnotations.initMocks(this)
//
//        viewModel = DetailViewModel()
//        viewModel.weatherRepository = repository
//        viewModel.schedulerProvider = TestSchedulerProvider()
//        viewModel.states.observeForever(view)
//    }
//
//    @Test
//    fun testGetLastWeather() {
//        val weather = mock(DailyForecastModel::class.java)
//        val id = "ID"
//
//        given(repository.getWeatherDetail(id)).willReturn(Single.just(weather))
//
//        viewModel.getDetail(id)
//
//        val arg = argumentCaptor<ViewModelState>()
//        // Here we expect 2 calls on view.onChanged
//        verify(view, times(2)).onChanged(arg.capture())
//
//        val values = arg.allValues
//        // Test obtained values in order
//        Assert.assertEquals(2, values.size)
//        Assert.assertEquals(LoadingState, values[0])
//        Assert.assertEquals(DetailViewModel.WeatherDetailState(weather), values[1])
//    }
//
//    @Test
//    fun testGeLasttWeatherFailed() {
//        val error = Throwable("Got error")
//        val id = "ID"
//
//        given(repository.getWeatherDetail(id)).willReturn(Single.error(error))
//
//        viewModel.getDetail(id)
//
//        val arg = argumentCaptor<ViewModelState>()
//        // Here we expect 2 calls on view.onChanged
//        verify(view, times(2)).onChanged(arg.capture())
//
//        val values = arg.allValues
//        // Test obtained values in order
//        Assert.assertEquals(2, values.size)
//        Assert.assertEquals(LoadingState, values[0])
//        Assert.assertEquals(ErrorState(error), values[1])
//    }
//}