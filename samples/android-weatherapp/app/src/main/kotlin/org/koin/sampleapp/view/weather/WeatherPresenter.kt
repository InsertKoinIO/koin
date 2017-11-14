package org.koin.sampleapp.view.weather

import io.reactivex.disposables.Disposable
import org.koin.sampleapp.repository.WeatherRepository
import org.koin.sampleapp.util.rx.SchedulerProvider

/**
 * Weather Presenter
 */
class WeatherPresenter(val weatherRepository: WeatherRepository, val schedulerProvider: SchedulerProvider) : WeatherContract.Presenter {

    override lateinit var view: WeatherContract.View
    private var currentRequest: Disposable? = null

    override fun start() {

    }

    override fun stop() {
        currentRequest?.dispose()
    }

    override fun getWeather(location: String) {
        currentRequest?.dispose()
        currentRequest = weatherRepository.getWeather(location)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                        { weather -> view.displayWeather(weather, location) },
                        { error -> view.displayError(error) }
                )
    }

}