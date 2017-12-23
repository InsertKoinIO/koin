package org.koin.sampleapp.repository

import io.reactivex.Single
import org.koin.sampleapp.repository.json.getLocation
import org.koin.sampleapp.repository.json.weather.Weather

/**
 * Weather repository
 */
interface WeatherRepository {
    fun getWeather(location: String): Single<Weather>
}

/**
 * Weather repository
 * Make use of WeatherDatasource & add some cache
 */
class WeatherRepositoryImpl(val weatherDatasource: WeatherDatasource) : WeatherRepository {

    private val DEFAULT_LANG = "EN"

    var weatherCache: Pair<String, Weather?>? = null

    override fun getWeather(location: String): Single<Weather> {
        val cache = weatherCache
        return if (cache?.first == location && cache.second != null) {
            Single.just(cache.second)
        } else {
            weatherDatasource.geocode(location)
                    .map { it.getLocation() ?: throw IllegalStateException("No Location data") }
                    .flatMap { weatherDatasource.weather(it.lat, it.lng, DEFAULT_LANG) }
                    .doOnSuccess { weatherCache = Pair(location, it) }
        }
    }
}