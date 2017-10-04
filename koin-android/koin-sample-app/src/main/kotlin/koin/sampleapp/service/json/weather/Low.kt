package koin.sampleapp.service.json.weather

import com.google.gson.annotations.Expose

class Low {

    /**
     * @return The fahrenheit
     */
    /**
     * @param fahrenheit The fahrenheit
     */
    @Expose
    var fahrenheit: String? = null
    /**
     * @return The celsius
     */
    /**
     * @param celsius The celsius
     */
    @Expose
    var celsius: String? = null

}
