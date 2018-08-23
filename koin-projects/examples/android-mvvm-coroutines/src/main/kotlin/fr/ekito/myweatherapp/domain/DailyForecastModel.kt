package fr.ekito.myweatherapp.domain

import fr.ekito.myweatherapp.data.datasource.webservice.json.weather.Forecastday_
import java.util.*

/**
 * Represents our weather forecast for one day
 */
data class DailyForecastModel(
    val location: String,
    val day: String,
    val shortText: String,
    val fullText: String,
    val iconUrl: String,
    val icon: String,
    val temperature: Temperature,
    val wind: Wind,
    val humidity: Humidity,
    val id: String = UUID.randomUUID().toString()
) {
    val colorCode: Int by lazy {
        val avg = temperature.high.toInt()
        when {
            avg in 0..8 -> 1
            avg in 8..13 -> 2
            avg in 14..20 -> 3
            avg > 21 -> 4
            else -> 0
        }
    }

    companion object {
        fun from(location: String, f: Forecastday_) = DailyForecastModel(
            location,
            f.date?.weekday ?: "",
            f.conditions ?: "",
            "",
            f.iconUrl ?: "",
            getWeatherCode(f.icon ?: ""),
            Temperature(f.low?.celsius ?: "", f.high!!.celsius!!),
            Wind(f.avewind?.kph ?: 0, f.avewind?.dir ?: ""),
            Humidity(f.avehumidity ?: 0)
        )
    }
}

data class Wind(val kph: Int, val dir: String) {
    override fun toString(): String {
        return "$kph KPH $dir"
    }
}

data class Temperature(val low: String, val high: String) {

    override fun toString(): String {
        return "$low°C - $high°C"
    }
}

data class Humidity(val humidity: Int) {
    override fun toString(): String {
        return "$humidity %"
    }
}