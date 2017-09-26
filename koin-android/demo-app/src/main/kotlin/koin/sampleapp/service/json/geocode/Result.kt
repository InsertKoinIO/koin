package koin.sampleapp.service.json.geocode

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

class Result {

    /**
     * @return The addressComponents
     */
    /**
     * @param addressComponents The address_components
     */
    @SerializedName("address_components")
    @Expose
    var addressComponents: List<AddressComponent> = ArrayList()
    /**
     * @return The formattedAddress
     */
    /**
     * @param formattedAddress The formatted_address
     */
    @SerializedName("formatted_address")
    @Expose
    var formattedAddress: String? = null
    /**
     * @return The geometry
     */
    /**
     * @param geometry The geometry
     */
    @SerializedName("geometry")
    @Expose
    var geometry: Geometry? = null
    /**
     * @return The placeId
     */
    /**
     * @param placeId The place_id
     */
    @SerializedName("place_id")
    @Expose
    var placeId: String? = null
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
