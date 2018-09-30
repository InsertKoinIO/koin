package fr.ekito.myweatherapp.domain.ext

import fr.ekito.myweatherapp.data.json.Forecastday
import fr.ekito.myweatherapp.data.json.Forecastday_
import fr.ekito.myweatherapp.data.json.Weather
import fr.ekito.myweatherapp.domain.entity.DailyForecast
import fr.ekito.myweatherapp.domain.entity.PREFIX

/**
 * Extract Weather DailyForecast list from Weather
 */
fun Weather.getDailyForecasts(location: String): List<DailyForecast> {
    val txtList: List<Forecastday> = forecast?.txtForecast?.forecastday.orEmpty()
    return forecast?.simpleforecast?.forecastday.orEmpty()
        .map { f: Forecastday_ ->
            DailyForecast.from(
                location,
                f
            )
        }
        .map { f ->
            f.copy(
                fullText = txtList.firstOrNull { it.title ?: "" == f.day }?.fcttext ?: ""
            )
        }
        .filter { f -> !f.icon.startsWith(PREFIX) }
}