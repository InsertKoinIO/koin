package fr.ekito.myweatherapp.data.repository

import fr.ekito.myweatherapp.data.datasource.webservice.WeatherWebDatasource
import fr.ekito.myweatherapp.data.datasource.webservice.json.geocode.getLocation
import fr.ekito.myweatherapp.data.datasource.webservice.json.weather.getDailyForecasts
import fr.ekito.myweatherapp.domain.DailyForecastModel
import io.reactivex.Single

/**
 * Weather repository
 */
interface WeatherRepository {
    /**
     * Get weather from given location
     * if location is null, get last weather or default
     */
    fun getWeather(location: String? = null): Single<List<DailyForecastModel>>

    /**
     * Get weather for given id
     */
    fun getWeatherDetail(id: String): Single<DailyForecastModel>
}

/**
 * Weather repository
 * Make use of WeatherWebDatasource & add some cache
 */
class WeatherRepositoryImpl(private val weatherDatasource: WeatherWebDatasource) :
    WeatherRepository {

    private fun lastLocationFromCache() = weatherCache.firstOrNull()?.location

    private val weatherCache = arrayListOf<DailyForecastModel>()

    override fun getWeatherDetail(id: String): Single<DailyForecastModel> =
        Single.just(weatherCache.first { it.id == id })

    override fun getWeather(
        location: String?
    ): Single<List<DailyForecastModel>> {
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
