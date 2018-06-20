package fr.ekito.myweatherapp.data.datasource.webservice.json.weather

import com.google.gson.annotations.Expose

data class QpfNight(
        @Expose var `in`: Double? = null,
        @Expose var mm: Int? = null
)
