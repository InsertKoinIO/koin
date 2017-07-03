package koin.sampleapp.json.weather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Forecast {

    /**
     * @return The txtForecast
     */
    /**
     * @param txtForecast The txt_forecast
     */
    @SerializedName("txt_forecast")
    @Expose
    var txtForecast: TxtForecast? = null
    /**
     * @return The simpleforecast
     */
    /**
     * @param simpleforecast The simpleforecast
     */
    @Expose
    var simpleforecast: Simpleforecast? = null

}
