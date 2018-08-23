package fr.ekito.myweatherapp.data.repository

import fr.ekito.myweatherapp.data.datasource.webservice.WeatherWebDatasource
import fr.ekito.myweatherapp.data.datasource.webservice.json.geocode.getLocation
import fr.ekito.myweatherapp.data.datasource.webservice.json.weather.getDailyForecasts
import fr.ekito.myweatherapp.domain.DailyForecastModel
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
    fun getWeather(location: String? = null): Deferred<List<DailyForecastModel>>

    /**
     * Get weather for given id
     */
    fun getWeatherDetail(id: String): Deferred<DailyForecastModel>
}

/**
 * Weather repository
 * Make use of WeatherWebDatasource & add some cache
 */
class WeatherRepositoryImpl(private val weatherDatasource: WeatherWebDatasource) :
    WeatherRepository {

    private fun lastLocationFromCache() = weatherCache.firstOrNull()?.location

    private val weatherCache = arrayListOf<DailyForecastModel>()

    override fun getWeatherDetail(id: String): Deferred<DailyForecastModel> = async {
        weatherCache.first { it.id == id }
    }

    override fun getWeather(location: String?): Deferred<List<DailyForecastModel>> = async {
        if (location == null && weatherCache.isNotEmpty()) weatherCache
        else {
            val targetLocation: String = location ?: lastLocationFromCache() ?: DEFAULT_LOCATION
            weatherCache.clear()
            val loc = weatherDatasource.geocode(targetLocation).await().getLocation() ?: error("")
            val forecasts =
                weatherDatasource.weather(loc.lat, loc.lng, DEFAULT_LANG).await().getDailyForecasts(targetLocation)
            weatherCache.addAll(forecasts)
            forecasts
        }
    }

    companion object {
        const val DEFAULT_LOCATION = "Paris"
        const val DEFAULT_LANG = "EN"
    }
}