package fr.ekito.myweatherapp.view.splash

import fr.ekito.myweatherapp.data.repository.WeatherRepository
import fr.ekito.myweatherapp.util.mvp.RxPresenter
import fr.ekito.myweatherapp.util.rx.SchedulerProvider
import fr.ekito.myweatherapp.util.rx.with

class SplashPresenter(
    private val weatherRepository: WeatherRepository,
    private val schedulerProvider: SchedulerProvider
) : RxPresenter<SplashContract.View>(), SplashContract.Presenter {

    override var view: SplashContract.View? = null

    override fun getLastWeather() {
        view?.showIsLoading()
        launch {
            weatherRepository.getWeather().with(schedulerProvider)
                .toCompletable()
                .subscribe({ view?.showIsLoaded() },
                    { error -> view?.showError(error) })
        }
    }
}