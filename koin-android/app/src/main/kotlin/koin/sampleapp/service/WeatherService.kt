package koin.sampleapp.service

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import koin.sampleapp.json.geocode.Geocode
import koin.sampleapp.json.weather.Weather

/**
 * Created by arnaud on 12/06/2017.
 */
class WeatherService(val weatherWS: WeatherWS) {

    fun getGeocode(address: String): Single<Geocode> {
        return weatherWS.geocode(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getWeather(lat: Double?, lon: Double?, lang: String = "EN"): Single<Weather> {
        return weatherWS.weather(lat, lon, lang).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}