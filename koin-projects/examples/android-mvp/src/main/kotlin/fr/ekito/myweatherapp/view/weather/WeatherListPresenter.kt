package fr.ekito.myweatherapp.view.weather

import fr.ekito.myweatherapp.domain.repository.DailyForecastRepository
import fr.ekito.myweatherapp.util.mvp.RxPresenter
import fr.ekito.myweatherapp.util.rx.SchedulerProvider
import fr.ekito.myweatherapp.util.rx.with
import fr.ekito.myweatherapp.view.weather.list.WeatherItem

/**
 * Weather Presenter
 */
class WeatherListPresenter(
    private val dailyForecastRepository: DailyForecastRepository,
    private val schedulerProvider: SchedulerProvider
) : RxPresenter<WeatherListContract.View>(), WeatherListContract.Presenter {

    override var view: WeatherListContract.View? = null

    override fun getWeatherList() {
        launch {
            dailyForecastRepository.getWeather()
                .with(schedulerProvider)
                .subscribe(
                    { weatherList ->
                        view?.showWeatherItemList(
                            weatherList.map { WeatherItem.from(it) }.takeLast(weatherList.size - 1)
                        )
                    },
                    { error -> view?.showError(error) })
        }
    }
}