package org.koin.sampleapp.repository

import io.reactivex.Single
import org.koin.sampleapp.repository.json.getLocation
import org.koin.sampleapp.repository.json.weather.Weather

/**
 * Weather repository
 * Make use of WeatherDatasource & add some cache
 */
class WeatherRepository(val weatherDatasource: WeatherDatasource) {

    private val DEFAULT_LANG = "EN"

    var weatherCache: Weather? = null

    fun getWeather(location: String): Single<Weather> {
        return weatherCache?.let { Single.just(weatherCache!!) } ?:
                weatherDatasource.geocode(location)
                        .map { it.getLocation() ?: throw IllegalStateException("No Location data") }
                        .flatMap { weatherDatasource.weather(it.lat, it.lng, DEFAULT_LANG) }
                        .doOnSuccess { weatherCache = it }
    }

    fun clearCache() {
        weatherCache = null
    }

}