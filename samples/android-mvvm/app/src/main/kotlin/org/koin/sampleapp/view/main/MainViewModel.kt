package org.koin.sampleapp.view.main

import org.koin.sampleapp.repository.WeatherRepository

class MainViewModel(val weatherRepository: WeatherRepository) {

    fun searchWeather(address: String) = weatherRepository.getWeather(address).toCompletable()

}