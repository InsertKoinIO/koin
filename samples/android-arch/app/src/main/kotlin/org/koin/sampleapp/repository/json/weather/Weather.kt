package org.koin.sampleapp.repository.json.weather

import com.google.gson.annotations.Expose

open class Weather {

    /**
     * @return The response
     */
    /**
     * @param response The response
     */
    @Expose
    var response: Response? = null
    /**
     * @return The forecast
     */
    /**
     * @param forecast The forecast
     */
    @Expose
    var forecast: Forecast? = null

}
