package org.koin.sampleapp.view.main

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.Disposable
import org.koin.sampleapp.repository.WeatherRepository
import org.koin.sampleapp.repository.json.weather.Weather
import org.koin.sampleapp.util.rx.SchedulerProvider
import org.koin.sampleapp.util.rx.with
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class MainViewModel() : ViewModel(), KoinComponent {
//(val weatherRepository: WeatherRepository,val scheduler: SchedulerProvider) : ViewModel() {

    val weatherRepository: WeatherRepository by inject()
    val scheduler: SchedulerProvider by inject()

    var disposable: Disposable? = null
    val searchOk = MutableLiveData<Weather>()

    fun searchWeather(address: String) {
        disposable = weatherRepository.getWeather(address)
                .with(scheduler)
                .subscribe({ w -> searchOk.value = w }, { err ->
                    System.err.println("MainViewModel : $err")
                })
    }

    override fun onCleared() {
        disposable?.dispose()
        super.onCleared()
    }

}