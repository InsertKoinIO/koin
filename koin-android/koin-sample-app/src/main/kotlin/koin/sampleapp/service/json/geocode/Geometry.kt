package koin.sampleapp.service.json.geocode

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Geometry {

    /**
     * @return The location
     */
    /**
     * @param location The location
     */
    @SerializedName("location")
    @Expose
    var location: Location? = null
    /**
     * @return The locationType
     */
    /**
     * @param locationType The location_type
     */
    @SerializedName("location_type")
    @Expose
    var locationType: String? = null
    /**
     * @return The viewport
     */
    /**
     * @param viewport The viewport
     */
    @SerializedName("viewport")
    @Expose
    var viewport: Viewport? = null

}
