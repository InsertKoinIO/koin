package org.koin.sampleapp.view.detail

import org.koin.sampleapp.repository.WeatherRepository

/**
 * Weather Presenter
 */
class WeatherDetailViewModel(val weatherRepository: WeatherRepository) {

    fun getDetail() = weatherRepository.getSelectDetail()
}