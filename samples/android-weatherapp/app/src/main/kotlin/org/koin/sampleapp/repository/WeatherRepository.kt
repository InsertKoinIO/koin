package org.koin.sampleapp.repository

import io.reactivex.Single
import org.koin.sampleapp.repository.json.getLocation
import org.koin.sampleapp.repository.json.weather.Weather

/**
 * Weather repository
 */
interface WeatherRepository{
    fun getWeather(location: String): Single<Weather>
    fun clearCache()
}

/**
 * Weather repository
 * Make use of WeatherDatasource & add some cache
 */
class WeatherRepositoryImpl(val weatherDatasource: WeatherDatasource) : WeatherRepository{

    private val DEFAULT_LANG = "EN"

    var weatherCache: Weather? = null

    override fun getWeather(location: String): Single<Weather> {
        return weatherCache?.let { Single.just(weatherCache!!) } ?:
                weatherDatasource.geocode(location)
                        .map { it.getLocation() ?: throw IllegalStateException("No Location data") }
                        .flatMap { weatherDatasource.weather(it.lat, it.lng, DEFAULT_LANG) }
                        .doOnSuccess { weatherCache = it }
    }

    override fun clearCache() {
        weatherCache = null
    }

}