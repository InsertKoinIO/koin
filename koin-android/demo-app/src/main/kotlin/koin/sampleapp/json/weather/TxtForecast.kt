package koin.sampleapp.json.weather

import com.google.gson.annotations.Expose

import java.util.ArrayList

class TxtForecast {

    /**
     * @return The date
     */
    /**
     * @param date The date
     */
    @Expose
    var date: String? = null
    /**
     * @return The forecastday
     */
    /**
     * @param forecastday The forecastday
     */
    @Expose
    var forecastday: List<Forecastday> = ArrayList()

}
