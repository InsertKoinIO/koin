package fr.ekito.myweatherapp.mock

import fr.ekito.myweatherapp.domain.entity.DailyForecast
import fr.ekito.myweatherapp.domain.entity.Humidity
import fr.ekito.myweatherapp.domain.entity.Temperature
import fr.ekito.myweatherapp.domain.entity.Wind

/**
 * Mock Weather Data
 */
object MockedData {

    val location = "Location"

    val mockList = listOf(
        DailyForecast(
            location,
            "d1",
            "",
            "",
            "",
            "",
            Temperature("1", "1"),
            Wind(0, ""),
            Humidity(0)
        ),
        DailyForecast(
            location,
            "d2",
            "",
            "",
            "",
            "",
            Temperature("1", "2"),
            Wind(0, ""),
            Humidity(0)
        ),
        DailyForecast(
            location,
            "d3",
            "",
            "",
            "",
            "",
            Temperature("3", "4"),
            Wind(0, ""),
            Humidity(0)
        )
    )
}