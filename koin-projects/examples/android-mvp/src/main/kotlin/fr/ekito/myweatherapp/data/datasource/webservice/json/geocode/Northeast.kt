package fr.ekito.myweatherapp.data.datasource.webservice.json.geocode

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Northeast(
        @SerializedName("lat") @Expose var lat: Double? = null,
        @SerializedName("lng") @Expose var lng: Double? = null
)
