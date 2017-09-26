package koin.sampleapp.service.json

import fr.ekito.myweatherapp.DailyForecastModel
import koin.sampleapp.service.json.weather.Weather

private val PREFIX = "nt_"

private val CHANCE_FLURRIES = "chanceflurries"  // wi-snow-wind
private val CHANCE_RAIN = "chancerain"          // wi-rain
private val CHANCE_SLEET = "chancesleet"        // wi-rain-mix
private val CHANCE_SNOW = "chancesnow"          // wi-snow
private val CHANCE_STORMS = "chancestorms"      // wi-thunderstorm

private val CLEAR = "clear"                     // wi-day-sunny
private val CLOUDY = "cloudy"                   // wi-cloudy
private val FLURRIES = "flurries"               // wi-snow-wind
private val FOG = "fog"                         // wi-fog
private val HAZY = "hazy"                       // wi-fog

private val MOSTLY_CLOUDY = "mostlycloudy"      // wi-day-cloudy
private val MOSTLY_SUNNY = "mostlysunny"        // wi-day-cloudy
private val PARTLY_CLOUDY = "partlycloudy"      // wi-day-cloudy
private val PARTLY_SUNNY = "partlysunny"        // wi-day-cloudy

private val RAIN = "rain"                       // wi-rain
private val SLEET = "sleet"                     // wi-rain-mix
private val SNOW = "snow"                       // wi-snow
private val SUNNY = "sunny"                     // wi-day-sunny
private val TSTORMS = "tstorms"                 // wi-thunderstorm

private val WI_SNOW_WIND = "{wi_snow_wind}"
private val WI_RAIN = "{wi_rain}"
private val WI_RAIN_MIX = "{wi_rain_mix}"
private val WI_SNOW = "{wi_snow}"
private val WI_THUNDERSTORM = "{wi_thunderstorm}"
private val WI_DAY_SUNNY = "{wi_day_sunny}"
private val WI_CLOUDY = "{wi_cloudy}"
private val WI_FOG = "{wi_fog}"
private val WI_DAY_CLOUDY = "{wi_day_cloudy}"

private fun getWeatherCode(icon: String): String {
    when (icon) {
        CHANCE_STORMS, PREFIX + CHANCE_STORMS, TSTORMS, PREFIX + TSTORMS -> return WI_THUNDERSTORM
        CHANCE_SNOW, PREFIX + CHANCE_SNOW, SNOW, PREFIX + SNOW -> return WI_SNOW
        CHANCE_FLURRIES, PREFIX + CHANCE_FLURRIES, FLURRIES, PREFIX + FLURRIES -> return WI_SNOW_WIND
        CHANCE_RAIN, PREFIX + CHANCE_RAIN, RAIN, PREFIX + RAIN -> return WI_RAIN
        CHANCE_SLEET, PREFIX + CHANCE_SLEET, SLEET, PREFIX + SLEET -> return WI_RAIN_MIX
        FOG, PREFIX + FOG, HAZY, PREFIX + HAZY -> return WI_FOG
        CLOUDY, PREFIX + CLOUDY -> return WI_CLOUDY
        MOSTLY_CLOUDY, PREFIX + MOSTLY_CLOUDY, MOSTLY_SUNNY, PREFIX + MOSTLY_SUNNY, PARTLY_CLOUDY, PREFIX + PARTLY_CLOUDY, PARTLY_SUNNY, PREFIX + PARTLY_SUNNY -> return WI_DAY_CLOUDY
        CLEAR, PREFIX + CLEAR, SUNNY, PREFIX + SUNNY -> return WI_DAY_SUNNY
        else -> return WI_DAY_CLOUDY
    }
}

fun Weather.getDailyForecasts(): List<DailyForecastModel> = forecast?.simpleforecast?.forecastday.orEmpty()
        .map { f -> DailyForecastModel(f.conditions!!, getWeatherCode(f.icon!!), f.low!!.celsius!!, f.high!!.celsius!!) }
        .filter { f -> !f.icon.startsWith(PREFIX) }
        .take(4)