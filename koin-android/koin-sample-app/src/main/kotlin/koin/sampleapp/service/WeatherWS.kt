package koin.sampleapp.service

import io.reactivex.Single
import koin.sampleapp.service.json.geocode.Geocode
import koin.sampleapp.service.json.weather.Weather
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface WeatherWS {

    @GET("/geocode")
    @Headers("Content-type: application/json")
    fun geocode(@Query("address") address: String): Single<Geocode>

    @GET("/weather")
    @Headers("Content-type: application/json")
    fun weather(@Query("lat") lat: Double?, @Query("lon") lon: Double?, @Query("lang") lang: String): Single<Weather>

}
