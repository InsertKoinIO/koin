package fr.ekito.myweatherapp.view.splash

import fr.ekito.myweatherapp.domain.repository.DailyForecastRepository
import fr.ekito.myweatherapp.util.mvp.RxPresenter
import fr.ekito.myweatherapp.util.coroutines.SchedulerProvider
import fr.ekito.myweatherapp.util.coroutines.with

class SplashPresenter(
    private val dailyForecastRepository: DailyForecastRepository,
    private val schedulerProvider: SchedulerProvider
) : RxPresenter<SplashContract.View>(), SplashContract.Presenter {

    override var view: SplashContract.View? = null

    override fun getLastWeather() {
        view?.showIsLoading()
        launch {
            dailyForecastRepository.getWeather().with(schedulerProvider)
                .toCompletable()
                .subscribe({ view?.showIsLoaded() },
                    { error -> view?.showError(error) })
        }
    }
}