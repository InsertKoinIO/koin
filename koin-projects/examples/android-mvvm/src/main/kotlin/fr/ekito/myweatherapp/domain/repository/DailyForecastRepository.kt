package fr.ekito.myweatherapp.domain.repository

import fr.ekito.myweatherapp.data.WeatherDataSource
import fr.ekito.myweatherapp.domain.entity.DailyForecast
import fr.ekito.myweatherapp.domain.ext.getDailyForecasts
import fr.ekito.myweatherapp.domain.ext.getLocation
import io.reactivex.Single

/**
 * Weather repository
 */
interface DailyForecastRepository {
    /**
     * Get weather from given location
     * if location is null, get last weather or default
     */
    fun getWeather(location: String? = null): Single<List<DailyForecast>>

    /**
     * Get weather for given id
     */
    fun getWeatherDetail(id: String): Single<DailyForecast>
}

/**
 * Weather repository
 * Make use of WeatherDataSource & add some cache
 */
class DailyForecastRepositoryImpl(private val weatherDatasource: WeatherDataSource) :
    DailyForecastRepository {

    private fun lastLocationFromCache() = weatherCache.firstOrNull()?.location

    private val weatherCache = arrayListOf<DailyForecast>()

    override fun getWeatherDetail(id: String): Single<DailyForecast> =
        Single.just(weatherCache.first { it.id == id })

    override fun getWeather(
        location: String?
    ): Single<List<DailyForecast>> {
        // Take cache
        return if (location == null && weatherCache.isNotEmpty()) return Single.just(weatherCache)
        else {
            val targetLocation: String = location ?: lastLocationFromCache() ?: DEFAULT_LOCATION
            weatherCache.clear()
            weatherDatasource.geocode(targetLocation)
                .map { it.getLocation() ?: throw IllegalStateException("No Location data") }
                .flatMap { weatherDatasource.weather(it.lat, it.lng, DEFAULT_LANG) }
                .map { it.getDailyForecasts(targetLocation) }
                .doOnSuccess { weatherCache.addAll(it) }
        }
    }

    companion object {
        const val DEFAULT_LOCATION = "Paris"
        const val DEFAULT_LANG = "EN"
    }
}
