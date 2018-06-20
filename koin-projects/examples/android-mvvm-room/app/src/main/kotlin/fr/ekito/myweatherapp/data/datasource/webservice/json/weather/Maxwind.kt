package fr.ekito.myweatherapp.data.datasource.webservice.json.weather

import com.google.gson.annotations.Expose

data class Maxwind(
        @Expose var mph: Int? = null,
        @Expose var kph: Int? = null,
        @Expose var dir: String? = null,
        @Expose var degrees: Int? = null
)
