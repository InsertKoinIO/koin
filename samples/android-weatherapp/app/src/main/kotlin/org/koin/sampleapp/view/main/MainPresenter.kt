package org.koin.sampleapp.view.main

import io.reactivex.disposables.Disposable
import org.koin.sampleapp.repository.WeatherRepository
import org.koin.sampleapp.util.rx.SchedulerProvider
import org.koin.sampleapp.util.rx.with

class MainPresenter(val weatherRepository: WeatherRepository, val schedulerProvider: SchedulerProvider) : MainContract.Presenter {

    override lateinit var view: MainContract.View

    var request: Disposable? = null

    override fun clear() {
        weatherRepository.clearCache()
    }

    override fun stop() {
        request?.dispose()
    }

    override fun getWeather(address: String) {
        view.displayProgress()
        request?.dispose()
        request = weatherRepository.getWeather(address)
                .with(schedulerProvider)
                .subscribe({ _ ->
                    view.displayNormal()
                    view.onWeatherSuccess()
                }, { error ->
                    view.displayNormal()
                    view.onWeatherFailed(error)
                })
    }
}