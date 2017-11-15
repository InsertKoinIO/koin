package org.koin.sampleapp.view.main

import io.reactivex.disposables.Disposable
import org.koin.sampleapp.repository.WeatherRepository
import org.koin.sampleapp.util.rx.SchedulerProvider
import org.koin.sampleapp.util.rx.with
import org.koin.standalone.KoinComponent
import org.koin.standalone.bindProperty

class MainPresenter(val weatherRepository: WeatherRepository, val schedulerProvider: SchedulerProvider) : MainContract.Presenter, KoinComponent {

    override lateinit var view: MainContract.View

    var request: Disposable? = null

    override fun start() {
    }

    override fun stop() {
        request?.dispose()
    }

    override fun getWeather(address: String) {
        request?.dispose()
        request = weatherRepository.getWeather(address)
                .with(schedulerProvider)
                .subscribe({ _ ->
                    bindProperty(PROPERTY_ADDRESS, address)
                    view.onWeatherSuccess()
                },
                        { error -> view.onWeatherFailed(error) })
    }
}