package org.koin.sampleapp.repository.local

import org.koin.sampleapp.repository.json.geocode.Geocode
import org.koin.sampleapp.repository.json.geocode.Location
import org.koin.sampleapp.repository.json.weather.Weather

/**
 * Created by arnaud on 12/10/2017.
 */
interface JsonReader {
    fun getAllLocations(): Map<Location, String>
    fun getWeather(name: String): Weather
    fun getGeocode(name: String): Geocode
}