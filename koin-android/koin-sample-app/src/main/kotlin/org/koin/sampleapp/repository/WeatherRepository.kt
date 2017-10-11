package org.koin.sampleapp.repository

import org.koin.sampleapp.repository.json.getLocation

/**
 * Created by arnaud on 11/10/2017.
 */
class WeatherRepository(val weatherDatasource: WeatherDatasource) {

    private val DEFAULT_LANG = "EN"

    fun getWeather(location: String) = weatherDatasource.geocode(location)
            .map { it.getLocation() ?: throw IllegalStateException("No Location data") }
            .flatMap { location -> weatherDatasource.weather(location.lat, location.lng, DEFAULT_LANG) }

}