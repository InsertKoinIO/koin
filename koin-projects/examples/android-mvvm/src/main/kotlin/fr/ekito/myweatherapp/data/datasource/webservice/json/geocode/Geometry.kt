package fr.ekito.myweatherapp.data.datasource.webservice.json.geocode

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Geometry(
        @SerializedName("location") @Expose var location: Location? = null,
        @SerializedName("location_type") @Expose var locationType: String? = null,
        @SerializedName("viewport") @Expose var viewport: Viewport? = null
)
