package fr.ekito.myweatherapp.data.datasource.webservice.json.weather

import com.google.gson.annotations.Expose

data class SimpleForecast(@Expose var forecastday: List<Forecastday_> = emptyList())
