package org.koin.sampleapp.view.weather

import io.reactivex.Single
import org.koin.sampleapp.model.DailyForecastModel
import org.koin.sampleapp.repository.WeatherRepository
import org.koin.sampleapp.repository.json.getDailyForecasts

/**
 * Weather Presenter
 */
class WeatherResultViewModel(val weatherRepository: WeatherRepository) {

    fun getWeatherList(address: String): Single<List<DailyForecastModel>> = weatherRepository.getWeather(address).map { it.getDailyForecasts() }

    fun selectDetail(detail: DailyForecastModel) = weatherRepository.selectDetail(detail)
}