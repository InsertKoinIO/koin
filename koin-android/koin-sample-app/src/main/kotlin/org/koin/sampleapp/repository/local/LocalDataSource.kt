package org.koin.sampleapp.repository.local

import io.reactivex.Single
import org.koin.sampleapp.repository.WeatherDatasource
import org.koin.sampleapp.repository.json.geocode.Geocode
import org.koin.sampleapp.repository.json.geocode.Location
import org.koin.sampleapp.repository.json.weather.Weather

class LocalDataSource(val jsonReader: JsonReader) : WeatherDatasource {
    private val cities = HashMap<Location, String>()
    private val default_city = "toulouse"

    init {
        cities += jsonReader.getAllLocations()
    }

    private fun isKnownCity(adrs: String): Boolean = cities.values.contains(adrs)

    private fun cityFromLocation(lat: Double?, lng: Double?): String {
        var city = "toulouse"
        cities.keys
                .filter { it.lat == lat && it.lng == lng }
                .forEach { city = cities[it]!! }

        return city
    }

    override fun geocode(address: String): Single<Geocode> {
        return Single.create { e ->
            val adrs = address.toLowerCase()
            val geocode: Geocode
            if (isKnownCity(adrs)) {
                geocode = jsonReader.getGeocode(adrs)
            } else {
                geocode = jsonReader.getGeocode(default_city)
            }
            e.onSuccess(geocode)
        }
    }

    override fun weather(lat: Double?, lon: Double?, lang: String): Single<Weather> {
        return Single.create { e ->
            val city = cityFromLocation(lat, lon)
            val weather = jsonReader.getWeather(city)
            e.onSuccess(weather)
        }
    }
}