package fr.ekito.myweatherapp.mock

import fr.ekito.myweatherapp.domain.DailyForecastModel
import fr.ekito.myweatherapp.domain.Humidity
import fr.ekito.myweatherapp.domain.Temperature
import fr.ekito.myweatherapp.domain.Wind

/**
 * Mock Weather Data
 */
object MockedData {

    val location = "Location"

    val mockList = listOf(
        DailyForecastModel(
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
        DailyForecastModel(
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
        DailyForecastModel(
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