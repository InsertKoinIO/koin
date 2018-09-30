package fr.ekito.myweatherapp.data.local

import fr.ekito.myweatherapp.data.WeatherDatasource
import fr.ekito.myweatherapp.data.json.Geocode
import fr.ekito.myweatherapp.data.json.Weather
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import java.util.concurrent.TimeUnit

/**
 * Read json files and render weather date
 */
class LocalFileDataSource(val jsonReader: JsonReader, val delayed: Boolean) :
    WeatherDatasource {

    private val cities by lazy { jsonReader.getAllLocations() }

    private fun isKnownCity(address: String): Boolean = cities.values.contains(address)

    private fun cityFromLocation(lat: Double?, lng: Double?): String {
        return cities.filterKeys { it.lat == lat && it.lng == lng }.values.firstOrNull()
            ?: DEFAULT_CITY
    }

    override fun geocode(address: String): Deferred<Geocode> = async {
        val addressToLC = address.toLowerCase()
        val geocode = if (isKnownCity(addressToLC)) {
            jsonReader.getGeocode(addressToLC)
        } else {
            jsonReader.getGeocode(DEFAULT_CITY)
        }
        if (delayed) {
            delay(1, TimeUnit.SECONDS)
        }
        geocode
    }

    override fun weather(lat: Double?, lon: Double?, lang: String): Deferred<Weather> = async {
        val city = cityFromLocation(lat, lon)
        val weather = jsonReader.getWeather(city)
        if (delayed) {
            delay(1, TimeUnit.SECONDS)
        }
        weather
    }

    companion object {
        const val DEFAULT_CITY = "toulouse"
    }
}