package koin.sampleapp.json.weather

import com.google.gson.annotations.Expose

class Maxwind {

    /**
     * @return The mph
     */
    /**
     * @param mph The mph
     */
    @Expose
    var mph: Int? = null
    /**
     * @return The kph
     */
    /**
     * @param kph The kph
     */
    @Expose
    var kph: Int? = null
    /**
     * @return The dir
     */
    /**
     * @param dir The dir
     */
    @Expose
    var dir: String? = null
    /**
     * @return The degrees
     */
    /**
     * @param degrees The degrees
     */
    @Expose
    var degrees: Int? = null

}
