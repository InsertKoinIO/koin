package fr.ekito.myweatherapp.data.datasource.webservice.json.weather

import com.google.gson.annotations.Expose

data class Weather(
        @Expose var response: Response? = null,
        @Expose var forecast: Forecast? = null
)
