package fr.ekito.myweatherapp.data.repository

import fr.ekito.myweatherapp.data.datasource.room.WeatherDAO
import fr.ekito.myweatherapp.data.datasource.room.WeatherEntity
import fr.ekito.myweatherapp.data.datasource.webservice.WeatherWebDatasource
import fr.ekito.myweatherapp.data.datasource.webservice.json.geocode.getLocation
import fr.ekito.myweatherapp.data.datasource.webservice.json.weather.getDailyForecasts
import fr.ekito.myweatherapp.domain.DailyForecastModel
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import java.util.*

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
class WeatherRepositoryImpl(
    private val weatherDatasource: WeatherWebDatasource,
    private val weatherDAO: WeatherDAO
) : WeatherRepository {

    override fun getWeatherDetail(id: String): Deferred<DailyForecastModel> = async {
        DailyForecastModel.from(weatherDAO.findWeatherById(id))
    }

    private fun getWeatherFromLatest(latest: WeatherEntity): Deferred<List<DailyForecastModel>> =
        async {
            weatherDAO.findAllBy(latest.location, latest.date)
                .map {
                    DailyForecastModel.from(it)
                }
        }

    override fun getWeather(
        location: String?
    ): Deferred<List<DailyForecastModel>> = async {
        val req = if (location == null) {
            val latest = weatherDAO.findLatestWeather()
            if (latest.isEmpty()) getNewWeather(DEFAULT_LOCATION)
            else getWeatherFromLatest(latest.first())
        } else {
            getNewWeather(location)
        }
        req.await()
    }

    private fun getNewWeather(location: String): Deferred<List<DailyForecastModel>> = async {
        val now = Date()
        val loc = weatherDatasource.geocode(location).await().getLocation() ?: error("No Location date")
        val geoloc = weatherDatasource.weather(loc.lat, loc.lng, DEFAULT_LANG).await()
        val list = geoloc.getDailyForecasts(location)
        async {
            weatherDAO.saveAll(list.map { item -> WeatherEntity.from(item, now) })
        }
        list
    }

    companion object {
        const val DEFAULT_LOCATION = "Paris"
        const val DEFAULT_LANG = "EN"
    }
}
