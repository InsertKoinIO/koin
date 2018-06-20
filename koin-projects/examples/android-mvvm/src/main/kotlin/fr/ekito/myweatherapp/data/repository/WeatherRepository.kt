package fr.ekito.myweatherapp.data.repository

import fr.ekito.myweatherapp.data.datasource.room.WeatherDAO
import fr.ekito.myweatherapp.data.datasource.room.WeatherEntity
import fr.ekito.myweatherapp.data.datasource.webservice.WeatherWebDatasource
import fr.ekito.myweatherapp.data.datasource.webservice.json.geocode.getLocation
import fr.ekito.myweatherapp.data.datasource.webservice.json.weather.getDailyForecasts
import fr.ekito.myweatherapp.domain.DailyForecastModel
import io.reactivex.Single
import java.util.*

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
class WeatherRepositoryImpl(
    private val weatherDatasource: WeatherWebDatasource,
    private val weatherDAO: WeatherDAO
) : WeatherRepository {

    override fun getWeatherDetail(id: String): Single<DailyForecastModel> {
        return weatherDAO.findWeatherById(id).map { DailyForecastModel.from(it) }
    }

    private fun getWeatherFromLatest(latest: WeatherEntity): Single<List<DailyForecastModel>> {
        return weatherDAO.findAllBy(latest.location, latest.date)
            .map {
                it.map { DailyForecastModel.from(it) }
            }
    }

    override fun getWeather(
        location: String?
    ): Single<List<DailyForecastModel>> {
        return if (location == null) {
            weatherDAO.findLatestWeather().flatMap { latest: List<WeatherEntity> ->
                if (latest.isEmpty()) getNewWeather(DEFAULT_LOCATION) else getWeatherFromLatest(
                    latest.first()
                )
            }
        } else {
            getNewWeather(location)
        }
    }

    private fun getNewWeather(location: String): Single<List<DailyForecastModel>> {
        val now = Date()
        return weatherDatasource.geocode(location)
            .map { it.getLocation() ?: throw IllegalStateException("No Location date") }
            .flatMap { weatherDatasource.weather(it.lat, it.lng, DEFAULT_LANG) }
            .map { it.getDailyForecasts(location) }
            .doOnSuccess { list ->
                weatherDAO.saveAll(list.map { item -> WeatherEntity.from(item, now) })
            }
    }

    companion object {
        const val DEFAULT_LOCATION = "Paris"
        const val DEFAULT_LANG = "EN"
    }
}
