package fr.ekito.myweatherapp.data.datasource.webservice.json.geocode

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Geocode(
        @SerializedName("results") @Expose var results: List<Result> = emptyList(),
        @SerializedName("status") @Expose var status: String? = null
)
