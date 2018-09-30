package fr.ekito.myweatherapp.mock.mvp

import fr.ekito.myweatherapp.domain.entity.DailyForecast
import fr.ekito.myweatherapp.domain.repository.DailyForecastRepository
import fr.ekito.myweatherapp.util.MockitoHelper
import fr.ekito.myweatherapp.util.TestSchedulerProvider
import fr.ekito.myweatherapp.view.detail.DetailContract
import fr.ekito.myweatherapp.view.detail.DetailPresenter
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class DetailPresenterMockTest {

    lateinit var presenter: DetailContract.Presenter
    @Mock
    lateinit var view: DetailContract.View
    @Mock
    lateinit var repository: DailyForecastRepository

    val id = "ID"

    // TODO uncomment to use LiveData in Test
//    @get:Rule
//    val rule = InstantTaskExecutorRule()

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)

        presenter = DetailPresenter(id, repository, TestSchedulerProvider())
        presenter.view = view
    }

    @Test
    fun testGetLastWeather() {
        val weather = mock(DailyForecast::class.java)

        given(repository.getWeatherDetail(id)).willReturn(Single.just(weather))

        presenter.getDetail()

        verify(view, never()).showError(MockitoHelper.any())
        verify(view).showDetail(weather)
    }

    @Test
    fun testGeLasttWeatherFailed() {
        val error = Throwable("Got error")

        given(repository.getWeatherDetail(id)).willReturn(Single.error(error))

        presenter.getDetail()

        verify(view, never()).showDetail(MockitoHelper.any())
        verify(view).showError(error)
    }
}