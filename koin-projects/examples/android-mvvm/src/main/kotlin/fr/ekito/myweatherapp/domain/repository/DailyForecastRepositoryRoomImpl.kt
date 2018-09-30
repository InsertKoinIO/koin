package fr.ekito.myweatherapp.domain.repository

import fr.ekito.myweatherapp.data.WeatherDataSource
import fr.ekito.myweatherapp.data.room.WeatherDAO
import fr.ekito.myweatherapp.data.room.WeatherEntity
import fr.ekito.myweatherapp.domain.entity.DailyForecast
import fr.ekito.myweatherapp.domain.ext.getDailyForecasts
import fr.ekito.myweatherapp.domain.ext.getLocation
import io.reactivex.Single
import java.util.*

class DailyForecastRepositoryRoomImpl(
    private val weatherDatasource: WeatherDataSource,
    private val weatherDAO: WeatherDAO
) : DailyForecastRepository {

    // Get weather from its id
    override fun getWeatherDetail(id: String): Single<DailyForecast> =
        weatherDAO.findWeatherById(id).map { DailyForecast.from(it) }

    // Get weather from latest or default one if the location is null
    // else get new weather
    override fun getWeather(
        location: String?
    ): Single<List<DailyForecast>> {
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

    // Get new weather and save it
    private fun getNewWeather(location: String): Single<List<DailyForecast>> {
        val now = Date()
        return weatherDatasource.geocode(location)
            .map { it.getLocation() ?: throw IllegalStateException("No Location date") }
            .flatMap { weatherDatasource.weather(it.lat, it.lng, DEFAULT_LANG) }
            .map { it.getDailyForecasts(location) }
            .doOnSuccess { list ->
                weatherDAO.saveAll(list.map { item -> WeatherEntity.from(item, now) })
            }
    }

    // Find latest weather
    private fun getWeatherFromLatest(latest: WeatherEntity): Single<List<DailyForecast>> {
        return weatherDAO.findAllBy(latest.location, latest.date)
            .map { list -> list.map { DailyForecast.from(it) } }
    }

    companion object {
        const val DEFAULT_LOCATION = "Paris"
        const val DEFAULT_LANG = "EN"
    }
}