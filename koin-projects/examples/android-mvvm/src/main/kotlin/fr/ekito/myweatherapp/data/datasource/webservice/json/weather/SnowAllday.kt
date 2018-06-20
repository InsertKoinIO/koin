package fr.ekito.myweatherapp.data.datasource.webservice.json.weather

import com.google.gson.annotations.Expose

data class SnowAllday(
        @Expose var `in`: Double? = null,
        @Expose var cm: Double? = null
)
