package fr.ekito.myweatherapp.view.weather

import fr.ekito.myweatherapp.data.repository.WeatherRepository
import fr.ekito.myweatherapp.util.mvp.RxPresenter
import fr.ekito.myweatherapp.util.rx.SchedulerProvider
import fr.ekito.myweatherapp.util.rx.with

class WeatherHeaderPresenter(
    private val weatherRepository: WeatherRepository,
    private val schedulerProvider: SchedulerProvider
) : RxPresenter<WeatherHeaderContract.View>(), WeatherHeaderContract.Presenter {

    override var view: WeatherHeaderContract.View? = null

    override fun loadNewLocation(location: String) {
        launch {
            weatherRepository.getWeather(location).toCompletable()
                .with(schedulerProvider)
                .subscribe(
                    { view?.showLocationSearchSucceed(location) },
                    { error -> view?.showLocationSearchFailed(location, error) })
        }
    }

    override fun getWeatherOfTheDay() {
        launch {
            weatherRepository.getWeather()
                .map { it.first() }
                .with(schedulerProvider)
                .subscribe(
                    { weather -> view?.showWeather(weather.location, weather) },
                    { error -> view?.showError(error) })
        }
    }
}