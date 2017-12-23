package org.koin.sampleapp.view.main

import io.reactivex.Completable
import org.koin.sampleapp.repository.WeatherRepository

class MainViewModel(val weatherRepository: WeatherRepository) {

    fun searchWeather(address: String): Completable = weatherRepository.getWeather(address).toCompletable()
}