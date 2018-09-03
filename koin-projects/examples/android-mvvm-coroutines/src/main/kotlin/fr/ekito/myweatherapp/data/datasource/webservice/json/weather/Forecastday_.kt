package fr.ekito.myweatherapp.data.datasource.webservice.json.weather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Forecastday_(
        @Expose var date: Date? = null,
        @Expose var period: Int? = null,
        @Expose var high: High? = null,
        @Expose var low: Low? = null,
        @Expose var conditions: String? = null,
        @Expose var icon: String? = null,
        @SerializedName("iconUrl") @Expose var iconUrl: String? = null,
        @Expose var skyicon: String? = null,
        @Expose var pop: Int? = null,
        @SerializedName("qpf_allday") @Expose var qpfAllday: QpfAllday? = null,
        @SerializedName("qpf_day") @Expose var qpfDay: QpfDay? = null,
        @SerializedName("qpf_night") @Expose var qpfNight: QpfNight? = null,
        @SerializedName("snow_allday") @Expose var snowAllday: SnowAllday? = null,
        @SerializedName("snow_day") @Expose var snowDay: SnowDay? = null,
        @SerializedName("snow_night") @Expose var snowNight: SnowNight? = null,
        @Expose var maxwind: Maxwind? = null,
        @Expose var avewind: Avewind? = null,
        @Expose var avehumidity: Int? = null,
        @Expose var maxhumidity: Int? = null,
        @Expose var minhumidity: Int? = null
)
