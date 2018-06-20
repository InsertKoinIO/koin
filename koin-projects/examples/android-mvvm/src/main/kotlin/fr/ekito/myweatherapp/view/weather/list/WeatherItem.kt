package fr.ekito.myweatherapp.view.weather.list

import fr.ekito.myweatherapp.domain.DailyForecastModel

/**
 * Result Item - Display in ResultList View Adapter
 */
data class WeatherItem(val id: String, val day: String, val icon: String, val color : Int) {
    companion object {
        fun from(weather: DailyForecastModel) = WeatherItem(
            weather.id,
            weather.day,
            weather.icon,
            weather.colorCode
        )
    }
}