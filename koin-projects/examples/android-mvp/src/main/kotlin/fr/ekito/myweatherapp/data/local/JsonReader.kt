package fr.ekito.myweatherapp.data.local

import fr.ekito.myweatherapp.data.json.Geocode
import fr.ekito.myweatherapp.data.json.Location
import fr.ekito.myweatherapp.data.json.Weather

/**
 * Json reader
 */
interface JsonReader {
    fun getAllLocations(): Map<Location, String>
    fun getWeather(name: String): Weather
    fun getGeocode(name: String): Geocode
}