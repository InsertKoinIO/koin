//package fr.ekito.myweatherapp.mock.mvp
//
//import fr.ekito.myweatherapp.data.repository.WeatherRepository
//import fr.ekito.myweatherapp.mock.MockedData.mockList
//import fr.ekito.myweatherapp.util.MockitoHelper
//import fr.ekito.myweatherapp.util.TestSchedulerProvider
//import fr.ekito.myweatherapp.view.weather.WeatherListContract
//import fr.ekito.myweatherapp.view.weather.WeatherListPresenter
//import fr.ekito.myweatherapp.view.weather.list.WeatherItem
//import io.reactivex.Single
//import org.junit.Before
//import org.junit.Test
//import org.mockito.BDDMockito.given
//import org.mockito.Mock
//import org.mockito.Mockito
//import org.mockito.Mockito.verify
//import org.mockito.MockitoAnnotations
//
//class WeatherListPresenterMockTest {
//
//    lateinit var presenter: WeatherListContract.Presenter
//    @Mock
//    lateinit var view: WeatherListContract.View
//    @Mock
//    lateinit var repository: WeatherRepository
//
//    @Before
//    fun before() {
//        MockitoAnnotations.initMocks(this)
//
//        presenter = WeatherListPresenter(repository, TestSchedulerProvider())
//        presenter.view = view
//    }
//
//    @Test
//    fun testDisplayList() {
//        val location = "DEFAULT_LOCATION"
//        given(repository.getWeather()).willReturn(Single.just(mockList))
//
//        presenter.getWeatherList()
//
//        val itemList = mockList.takeLast(mockList.size - 1).map { WeatherItem.from(it) }
//        verify(view, Mockito.never()).showError(MockitoHelper.any())
//        verify(view).showWeatherItemList(itemList)
//    }
//
//    @Test
//    fun testDisplayListFailed() {
//        val error = Throwable("Got an error")
//        val location = "DEFAULT_LOCATION"
//        given(repository.getWeather()).willReturn(Single.error(error))
//
//        presenter.getWeatherList()
//
//        verify(view, Mockito.never()).showWeatherItemList(MockitoHelper.any())
//        verify(view).showError(error)
//    }
//
//}