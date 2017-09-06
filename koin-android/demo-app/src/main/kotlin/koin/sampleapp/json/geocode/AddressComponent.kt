package koin.sampleapp.json.geocode

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

class AddressComponent {

    /**
     * @return The longName
     */
    /**
     * @param longName The long_name
     */
    @SerializedName("long_name")
    @Expose
    var longName: String? = null
    /**
     * @return The shortName
     */
    /**
     * @param shortName The short_name
     */
    @SerializedName("short_name")
    @Expose
    var shortName: String? = null
    /**
     * @return The types
     */
    /**
     * @param types The types
     */
    @SerializedName("types")
    @Expose
    var types: List<String> = ArrayList()

}
