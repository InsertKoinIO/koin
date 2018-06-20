package fr.ekito.myweatherapp.data.datasource.webservice.local

import fr.ekito.myweatherapp.data.datasource.webservice.json.geocode.Geocode
import fr.ekito.myweatherapp.data.datasource.webservice.json.geocode.Location
import fr.ekito.myweatherapp.data.datasource.webservice.json.weather.Weather

/**
 * Json reader
 */
interface JsonReader {
    fun getAllLocations(): Map<Location, String>
    fun getWeather(name: String): Weather
    fun getGeocode(name: String): Geocode
}