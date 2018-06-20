package fr.ekito.myweatherapp.data.datasource.webservice

import io.reactivex.Single
import fr.ekito.myweatherapp.data.datasource.webservice.json.geocode.Geocode
import fr.ekito.myweatherapp.data.datasource.webservice.json.weather.Weather
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * Weather datasource - Retrofit tagged
 */
interface WeatherWebDatasource {

    @GET("/geocode")
    @Headers("Content-type: application/json")
    fun geocode(@Query("location") address: String): Single<Geocode>

    @GET("/weather")
    @Headers("Content-type: application/json")
    fun weather(@Query("lat") lat: Double?, @Query("lon") lon: Double?, @Query("lang") lang: String): Single<Weather>

}
