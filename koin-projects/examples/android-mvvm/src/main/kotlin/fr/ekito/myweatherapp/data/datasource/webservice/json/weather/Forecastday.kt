package fr.ekito.myweatherapp.data.datasource.webservice.json.weather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ForecastDay(
        @Expose var period: Int? = null,
        @Expose var icon: String? = null,
        @SerializedName("iconUrl")
        @Expose var iconUrl: String? = null,
        @Expose var title: String? = null,
        @Expose var fcttext: String? = null,
        @SerializedName("fcttextMetric")
        @Expose var fcttextMetric: String? = null,
        @Expose var pop: String? = null
)
