package fr.ekito.myweatherapp.data.datasource.webservice.json.weather

import com.google.gson.annotations.Expose

data class High(
        @Expose var fahrenheit: String? = null,
        @Expose var celsius: String? = null
)
