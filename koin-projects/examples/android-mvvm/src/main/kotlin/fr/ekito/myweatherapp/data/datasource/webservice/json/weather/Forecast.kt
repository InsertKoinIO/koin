package fr.ekito.myweatherapp.data.datasource.webservice.json.weather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Forecast(
        @SerializedName("txtForecast") @Expose var txtForecast: TxtForecast? = null,
        @SerializedName("simpleforecast") @Expose var simpleForecast: SimpleForecast? = null
)
