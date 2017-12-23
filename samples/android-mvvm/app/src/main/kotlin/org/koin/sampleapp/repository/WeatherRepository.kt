package org.koin.sampleapp.repository

import io.reactivex.Completable
import io.reactivex.Single
import org.koin.sampleapp.model.DailyForecastModel
import org.koin.sampleapp.repository.json.getLocation
import org.koin.sampleapp.repository.json.weather.Weather

/**
 * Weather repository
 */
interface WeatherRepository {
    fun getWeather(location: String): Single<Weather>
    fun selectDetail(detail: DailyForecastModel): Completable
    fun getSelectDetail(): Single<DailyForecastModel>
}

/**
 * Weather repository
 * Make use of WeatherDatasource & add some cache
 */
class WeatherRepositoryImpl(val weatherDatasource: WeatherDatasource) : WeatherRepository {

    var weatherCache: Pair<String, Weather>? = null

    var detail: DailyForecastModel? = null


    override fun selectDetail(detail: DailyForecastModel) =
            Completable.create {
                    this.detail = detail
                    it.onComplete()
                }

    override fun getSelectDetail(): Single<DailyForecastModel> = if (detail != null) Single.just(detail) else Single.error(IllegalStateException("Detail is null ! You must select a detail before"))

    private val TAG = this::class.java.simpleName
    private val DEFAULT_LANG = "EN"


    override fun getWeather(location: String): Single<Weather> {
        val cache = weatherCache
        return if (cache?.first == location) {
            println(TAG + " use cache")
            Single.just(cache.second)
        } else {
            weatherDatasource.geocode(location)
                    .map { it.getLocation() ?: throw IllegalStateException("No Location data") }
                    .flatMap { weatherDatasource.weather(it.lat, it.lng, DEFAULT_LANG) }
                    .doOnSuccess { weatherCache = Pair(location, it) }
        }
    }
}