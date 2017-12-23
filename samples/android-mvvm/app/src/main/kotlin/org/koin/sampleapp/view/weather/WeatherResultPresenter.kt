package org.koin.sampleapp.view.weather

import io.reactivex.disposables.Disposable
import org.koin.sampleapp.repository.WeatherRepository
import org.koin.sampleapp.repository.json.getDailyForecasts
import org.koin.sampleapp.util.rx.SchedulerProvider
import org.koin.sampleapp.util.rx.with

/**
 * Weather Presenter
 */
class WeatherResultPresenter(val weatherRepository: WeatherRepository, val schedulerProvider: SchedulerProvider) : WeatherResultContract.Presenter {

    override lateinit var view: WeatherResultContract.View
    private var currentRequest: Disposable? = null

    override fun stop() {
        currentRequest?.dispose()
    }

    override fun getWeather(address: String) {
        currentRequest?.dispose()
        currentRequest = weatherRepository.getWeather(address)
                .with(schedulerProvider)
                .map { it.getDailyForecasts() }
                .subscribe(
                        { weatherList -> view.displayWeather(weatherList) },
                        { error -> view.displayError(error) })
    }
}