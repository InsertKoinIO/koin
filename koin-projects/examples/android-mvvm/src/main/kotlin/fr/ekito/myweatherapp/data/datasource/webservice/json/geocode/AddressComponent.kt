package fr.ekito.myweatherapp.data.datasource.webservice.json.geocode

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AddressComponent(
        @SerializedName("long_name") @Expose var longName: String? = null,
        @SerializedName("short_name") @Expose var shortName: String? = null,
        @SerializedName("types") @Expose var types: List<String> = emptyList()
)

