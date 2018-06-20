package fr.ekito.myweatherapp.data.datasource.webservice.json.weather

import com.google.gson.annotations.Expose

data class Response(
        @Expose var version: String? = null,
        @Expose var termsofService: String? = null,
        @Expose var features: Features? = null
)
