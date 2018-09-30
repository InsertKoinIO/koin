package fr.ekito.myweatherapp.mock.mvp

import fr.ekito.myweatherapp.domain.repository.DailyForecastRepository
import fr.ekito.myweatherapp.mock.MockedData.mockList
import fr.ekito.myweatherapp.util.MockitoHelper
import fr.ekito.myweatherapp.util.TestSchedulerProvider
import fr.ekito.myweatherapp.view.weather.WeatherHeaderContract
import fr.ekito.myweatherapp.view.weather.WeatherHeaderPresenter
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class WeatherHeaderPresenterMockTest {

    lateinit var presenter: WeatherHeaderContract.Presenter
    @Mock
    lateinit var view: WeatherHeaderContract.View
    @Mock
    lateinit var repository: DailyForecastRepository

    // TODO uncomment to use LiveData in Test
//    @get:Rule
//    val rule = InstantTaskExecutorRule()

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)

        presenter =
                WeatherHeaderPresenter(repository, TestSchedulerProvider())
        presenter.view = view
    }

    @Test
    fun testDisplayList() {
        val first = mockList.first()
        given(repository.getWeather()).willReturn(Single.just(mockList))

        presenter.getWeatherOfTheDay()
        verify(view, never()).showError(MockitoHelper.any())
        verify(view).showWeather(first.location, first)
    }

    @Test
    fun testDisplayListFailed() {
        val error = Throwable("Got an error")
        given(repository.getWeather(MockitoHelper.any())).willReturn(Single.error(error))

        presenter.getWeatherOfTheDay()

        verify(view, never()).showWeather(MockitoHelper.any(), MockitoHelper.any())
        verify(view).showError(error)
    }

    @Test
    fun testSearchNewLocation() {
        val location = "new location"
        given(repository.getWeather(location)).willReturn(Single.just(mockList))
        presenter.loadNewLocation(location)

        verify(view, never()).showLocationSearchFailed(MockitoHelper.any(), MockitoHelper.any())
        verify(view).showLocationSearchSucceed(location)
    }

    @Test
    fun testSearchNewLocationFailed() {
        val location = "new location"
        val error = Throwable("Got an error")

        given(repository.getWeather(location)).willReturn(Single.error(error))
        presenter.loadNewLocation(location)
        verify(view, never()).showLocationSearchSucceed(MockitoHelper.any())
        verify(view).showLocationSearchFailed(location, error)
    }

}