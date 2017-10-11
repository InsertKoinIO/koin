package org.koin.sampleapp.weather

import io.reactivex.disposables.Disposable
import org.koin.sampleapp.repository.json.getLocation
import org.koin.sampleapp.repository.remote.WeatherDatasource
import org.koin.sampleapp.rx.SchedulerProvider

/**
 * Weather Presenter
 */
class WeatherPresenter(val weatherWS: WeatherDatasource, val schedulerProvider: SchedulerProvider) : WeatherContract.Presenter {

    override lateinit var view: WeatherContract.View
    private var currentRequest: Disposable? = null
    private val DEFAULT_LANG = "EN"

    override fun start() {

    }

    override fun stop() {
        currentRequest?.dispose()
    }

    override fun getWeather(location: String) {
        currentRequest?.dispose()
        currentRequest = weatherWS.geocode(location)
                .map { it.getLocation() ?: throw IllegalStateException("No Location data") }
                .flatMap { location -> weatherWS.weather(location.lat, location.lng, DEFAULT_LANG) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                        { weather -> view.displayWeather(weather, location) },
                        { error -> view.displayError(error) }
                )
    }

}