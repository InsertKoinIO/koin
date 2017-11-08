package org.koin.sampleapp.repository.local

import org.koin.sampleapp.repository.json.geocode.Geocode
import org.koin.sampleapp.repository.json.geocode.Location
import org.koin.sampleapp.repository.json.weather.Weather

interface JsonReader {
    fun getAllLocations(): Map<Location, String>
    fun getWeather(name: String): Weather
    fun getGeocode(name: String): Geocode
}