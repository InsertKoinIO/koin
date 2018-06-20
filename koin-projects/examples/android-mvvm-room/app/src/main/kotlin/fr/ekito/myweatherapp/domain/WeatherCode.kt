package fr.ekito.myweatherapp.domain

import android.content.Context
import fr.ekito.myweatherapp.R
import fr.ekito.myweatherapp.view.weather.list.WeatherItem

internal const val PREFIX = "nt_"

const val CHANCE_FLURRIES = "chanceflurries"  // wi-snow-wind
const val CHANCE_RAIN = "chancerain"          // wi-rain
const val CHANCE_SLEET = "chancesleet"        // wi-rain-mix
const val CHANCE_SNOW = "chancesnow"          // wi-snow
const val CHANCE_STORMS = "chancestorms"      // wi-thunderstorm

const val CLEAR = "clear"                     // wi-day-sunny
const val CLOUDY = "cloudy"                   // wi-cloudy
const val FLURRIES = "flurries"               // wi-snow-wind
const val FOG = "fog"                         // wi-fog
const val HAZY = "hazy"                       // wi-fog

const val MOSTLY_CLOUDY = "mostlycloudy"      // wi-day-cloudy
const val MOSTLY_SUNNY = "mostlysunny"        // wi-day-cloudy
const val PARTLY_CLOUDY = "partlycloudy"      // wi-day-cloudy
const val PARTLY_SUNNY = "partlysunny"        // wi-day-cloudy

const val RAIN = "rain"                       // wi-rain
const val SLEET = "sleet"                     // wi-rain-mix
const val SNOW = "snow"                       // wi-snow
const val SUNNY = "sunny"                     // wi-day-sunny
const val TSTORMS = "tstorms"                 // wi-thunderstorm

const val WI_SNOW_WIND = "{wi_snow_wind}"
const val WI_RAIN = "{wi_rain}"
const val WI_RAIN_MIX = "{wi_rain_mix}"
const val WI_SNOW = "{wi_snow}"
const val WI_THUNDERSTORM = "{wi_thunderstorm}"
const val WI_DAY_SUNNY = "{wi_day_sunny}"
const val WI_CLOUDY = "{wi_cloudy}"
const val WI_FOG = "{wi_fog}"
const val WI_DAY_CLOUDY = "{wi_day_cloudy}"

fun getWeatherCode(icon: String): String {
    return when (icon) {
        CHANCE_STORMS, PREFIX + CHANCE_STORMS, TSTORMS, PREFIX + TSTORMS -> WI_THUNDERSTORM
        CHANCE_SNOW, PREFIX + CHANCE_SNOW, SNOW, PREFIX + SNOW -> WI_SNOW
        CHANCE_FLURRIES, PREFIX + CHANCE_FLURRIES, FLURRIES, PREFIX + FLURRIES -> WI_SNOW_WIND
        CHANCE_RAIN, PREFIX + CHANCE_RAIN, RAIN, PREFIX + RAIN -> WI_RAIN
        CHANCE_SLEET, PREFIX + CHANCE_SLEET, SLEET, PREFIX + SLEET -> WI_RAIN_MIX
        FOG, PREFIX + FOG, HAZY, PREFIX + HAZY -> WI_FOG
        CLOUDY, PREFIX + CLOUDY -> WI_CLOUDY
        MOSTLY_CLOUDY, PREFIX + MOSTLY_CLOUDY, MOSTLY_SUNNY, PREFIX + MOSTLY_SUNNY, PARTLY_CLOUDY, PREFIX + PARTLY_CLOUDY, PARTLY_SUNNY, PREFIX + PARTLY_SUNNY -> WI_DAY_CLOUDY
        CLEAR, PREFIX + CLEAR, SUNNY, PREFIX + SUNNY -> WI_DAY_SUNNY
        else -> WI_DAY_CLOUDY
    }
}

@Suppress("DEPRECATION")
fun Context.getColorFromCode(w: DailyForecastModel): Int {
    return when (w.colorCode) {
        1 -> resources.getColor(R.color.temp_1)
        2 -> resources.getColor(R.color.temp_2)
        3 -> resources.getColor(R.color.temp_3)
        4 -> resources.getColor(R.color.temp_4)
        else -> resources.getColor(R.color.temp_0)
    }
}

@Suppress("DEPRECATION")
fun Context.getColorFromCode(w: WeatherItem): Int {
    return when (w.color) {
        1 -> resources.getColor(R.color.temp_1)
        2 -> resources.getColor(R.color.temp_2)
        3 -> resources.getColor(R.color.temp_3)
        4 -> resources.getColor(R.color.temp_4)
        else -> resources.getColor(R.color.temp_0)
    }
}