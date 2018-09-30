package fr.ekito.myweatherapp.domain.repository

import fr.ekito.myweatherapp.data.WeatherDatasource
import fr.ekito.myweatherapp.domain.entity.DailyForecast
import fr.ekito.myweatherapp.domain.ext.getDailyForecasts
import fr.ekito.myweatherapp.domain.ext.getLocation
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * Weather repository
 */
interface WeatherRepository {
    /**
     * Get weather from given location
     * if location is null, get last weather or default
     */
    fun getWeather(location: String? = null): Deferred<List<DailyForecast>>

    /**
     * Get weather for given id
     */
    fun getWeatherDetail(id: String): Deferred<DailyForecast>
}

/**
 * Weather repository
 * Make use of WeatherDatasource & add some cache
 */
class WeatherRepositoryImpl(private val weatherDatasource: WeatherDatasource) :
    WeatherRepository {

    private fun lastLocationFromCache() = weatherCache.firstOrNull()?.location

    private val weatherCache = arrayListOf<DailyForecast>()

    override fun getWeatherDetail(id: String): Deferred<DailyForecast> = async {
        weatherCache.first { it.id == id }
    }

    override fun getWeather(location: String?): Deferred<List<DailyForecast>> = async {
        if (location == null && weatherCache.isNotEmpty()) weatherCache
        else {
            val targetLocation: String = location ?: lastLocationFromCache() ?: DEFAULT_LOCATION
            weatherCache.clear()
            val loc = weatherDatasource.geocode(targetLocation).await().getLocation() ?: error("")
            val forecasts =
                weatherDatasource.weather(loc.lat, loc.lng, DEFAULT_LANG).await()
                    .getDailyForecasts(targetLocation)
            weatherCache.addAll(forecasts)
            forecasts
        }
    }

    companion object {
        const val DEFAULT_LOCATION = "Paris"
        const val DEFAULT_LANG = "EN"
    }
}