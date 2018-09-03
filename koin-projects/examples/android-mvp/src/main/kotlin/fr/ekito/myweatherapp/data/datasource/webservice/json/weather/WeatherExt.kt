package fr.ekito.myweatherapp.data.datasource.webservice.json.weather

import fr.ekito.myweatherapp.domain.DailyForecastModel
import fr.ekito.myweatherapp.domain.PREFIX

/**
 * Extract Weather DailyForecastModel list from Weather
 */
fun Weather.getDailyForecasts(location: String): List<DailyForecastModel> {
    val txtList: List<ForecastDay> = forecast?.txtForecast?.forecastday.orEmpty()
    return forecast?.simpleForecast?.forecastday.orEmpty()
        .map { f: Forecastday_ -> DailyForecastModel.from(location, f) }
        .map { f ->
            f.copy(
                fullText = txtList.firstOrNull { it.title ?: "" == f.day }?.fcttext ?: ""
            )
        }
        .filter { f -> !f.icon.startsWith(PREFIX) }

}