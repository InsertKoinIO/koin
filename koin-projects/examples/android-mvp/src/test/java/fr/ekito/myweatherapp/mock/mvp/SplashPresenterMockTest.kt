package fr.ekito.myweatherapp.mock.mvp

import fr.ekito.myweatherapp.domain.entity.DailyForecast
import fr.ekito.myweatherapp.domain.repository.DailyForecastRepository
import fr.ekito.myweatherapp.util.MockitoHelper
import fr.ekito.myweatherapp.util.TestSchedulerProvider
import fr.ekito.myweatherapp.view.splash.SplashContract
import fr.ekito.myweatherapp.view.splash.SplashPresenter
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class SplashPresenterMockTest {

    lateinit var presenter: SplashContract.Presenter
    @Mock
    lateinit var view: SplashContract.View
    @Mock
    lateinit var repository: DailyForecastRepository

    // TODO uncomment to use LiveData in Test
//    @get:Rule
//    val rule = InstantTaskExecutorRule()

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)

        presenter = SplashPresenter(repository, TestSchedulerProvider())
        presenter.view = view
    }

    @Test
    fun testGetLastWeather() {
        val list = listOf(mock(DailyForecast::class.java))

        given(repository.getWeather()).willReturn(Single.just(list))

        presenter.getLastWeather()

        verify(view, never()).showError(MockitoHelper.any())
        inOrder(view).apply {
            verify(view).showIsLoading()
            verify(view).showIsLoaded()
        }
    }

    @Test
    fun testGetLasttWeatherFailed() {
        val error = Throwable("Got an error")
        given(repository.getWeather()).willReturn(Single.error(error))

        presenter.getLastWeather()

        inOrder(view).apply {
            verify(view).showIsLoading()
            verify(view).showError(error)
        }
        verify(view, never()).showIsLoaded()
    }

    companion object {
        const val DEFAULT_LOCATION = "DEFAULT_LOCATION"
    }
}