package koin.sampleapp.service.json.weather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Forecastday {

    /**
     * @return The period
     */
    /**
     * @param period The period
     */
    @Expose
    var period: Int? = null
    /**
     * @return The icon
     */
    /**
     * @param icon The icon
     */
    @Expose
    var icon: String? = null
    /**
     * @return The iconUrl
     */
    /**
     * @param iconUrl The icon_url
     */
    @SerializedName("icon_url")
    @Expose
    var iconUrl: String? = null
    /**
     * @return The title
     */
    /**
     * @param title The title
     */
    @Expose
    var title: String? = null
    /**
     * @return The fcttext
     */
    /**
     * @param fcttext The fcttext
     */
    @Expose
    var fcttext: String? = null
    /**
     * @return The fcttextMetric
     */
    /**
     * @param fcttextMetric The fcttext_metric
     */
    @SerializedName("fcttext_metric")
    @Expose
    var fcttextMetric: String? = null
    /**
     * @return The pop
     */
    /**
     * @param pop The pop
     */
    @Expose
    var pop: String? = null

}
