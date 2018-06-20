package fr.ekito.myweatherapp.data.datasource.webservice.json.weather

import com.google.gson.annotations.Expose

data class TxtForecast(
        @Expose var date: String? = null,
        @Expose var forecastday: List<ForecastDay> = emptyList()
)
