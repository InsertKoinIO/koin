package org.koin.sampleapp.repository.local

import com.google.gson.Gson
import org.koin.sampleapp.repository.json.geocode.Geocode
import org.koin.sampleapp.repository.json.geocode.Location
import org.koin.sampleapp.repository.json.weather.Weather

/**
 * Created by arnaud on 12/10/2017.
 */
abstract class BaseReader : JsonReader {

    private val gson = Gson()
    private val geocode_prefix = "geocode_"
    private val weather_prefix = "weather_"
    private val json_file = ".json"

    override fun getAllLocations(): Map<Location, String> {
        val list = getAllFiles()
        return list.filter { it.startsWith(geocode_prefix) }.map {
            val name = it.replace(geocode_prefix, "").replace(".json", "")
            val geocode = getGeocode(name)
            // pair result
            Pair(geocode.results[0].geometry!!.location!!, name)
        }.toMap() // direct to map
    }

    override fun getGeocode(name: String): Geocode = gson.fromJson<Geocode>(readJsonFile(geocode_prefix + name + json_file), Geocode::class.java)

    override fun getWeather(name: String): Weather = gson.fromJson<Weather>(readJsonFile(weather_prefix + name + json_file), Weather::class.java)

    abstract fun getAllFiles(): List<String>

    abstract fun readJsonFile(jsonFile: String): String
}