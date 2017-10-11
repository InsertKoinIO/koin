package org.koin.sampleapp.repository.json.weather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Forecastday_ {

    /**
     * @return The date
     */
    /**
     * @param date The date
     */
    @Expose
    var date: Date? = null
    /**
     * @return The period
     */
    /**
     * @param period The period
     */
    @Expose
    var period: Int? = null
    /**
     * @return The high
     */
    /**
     * @param high The high
     */
    @Expose
    var high: High? = null
    /**
     * @return The low
     */
    /**
     * @param low The low
     */
    @Expose
    var low: Low? = null
    /**
     * @return The conditions
     */
    /**
     * @param conditions The conditions
     */
    @Expose
    var conditions: String? = null
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
     * @return The skyicon
     */
    /**
     * @param skyicon The skyicon
     */
    @Expose
    var skyicon: String? = null
    /**
     * @return The pop
     */
    /**
     * @param pop The pop
     */
    @Expose
    var pop: Int? = null
    /**
     * @return The qpfAllday
     */
    /**
     * @param qpfAllday The qpf_allday
     */
    @SerializedName("qpf_allday")
    @Expose
    var qpfAllday: QpfAllday? = null
    /**
     * @return The qpfDay
     */
    /**
     * @param qpfDay The qpf_day
     */
    @SerializedName("qpf_day")
    @Expose
    var qpfDay: QpfDay? = null
    /**
     * @return The qpfNight
     */
    /**
     * @param qpfNight The qpf_night
     */
    @SerializedName("qpf_night")
    @Expose
    var qpfNight: QpfNight? = null
    /**
     * @return The snowAllday
     */
    /**
     * @param snowAllday The snow_allday
     */
    @SerializedName("snow_allday")
    @Expose
    var snowAllday: SnowAllday? = null
    /**
     * @return The snowDay
     */
    /**
     * @param snowDay The snow_day
     */
    @SerializedName("snow_day")
    @Expose
    var snowDay: SnowDay? = null
    /**
     * @return The snowNight
     */
    /**
     * @param snowNight The snow_night
     */
    @SerializedName("snow_night")
    @Expose
    var snowNight: SnowNight? = null
    /**
     * @return The maxwind
     */
    /**
     * @param maxwind The maxwind
     */
    @Expose
    var maxwind: Maxwind? = null
    /**
     * @return The avewind
     */
    /**
     * @param avewind The avewind
     */
    @Expose
    var avewind: Avewind? = null
    /**
     * @return The avehumidity
     */
    /**
     * @param avehumidity The avehumidity
     */
    @Expose
    var avehumidity: Int? = null
    /**
     * @return The maxhumidity
     */
    /**
     * @param maxhumidity The maxhumidity
     */
    @Expose
    var maxhumidity: Int? = null
    /**
     * @return The minhumidity
     */
    /**
     * @param minhumidity The minhumidity
     */
    @Expose
    var minhumidity: Int? = null

}
