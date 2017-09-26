package koin.sampleapp.service.json.weather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Date {

    /**
     * @return The epoch
     */
    /**
     * @param epoch The epoch
     */
    @Expose
    var epoch: String? = null
    /**
     * @return The pretty
     */
    /**
     * @param pretty The pretty
     */
    @Expose
    var pretty: String? = null
    /**
     * @return The day
     */
    /**
     * @param day The day
     */
    @Expose
    var day: Int? = null
    /**
     * @return The month
     */
    /**
     * @param month The month
     */
    @Expose
    var month: Int? = null
    /**
     * @return The year
     */
    /**
     * @param year The year
     */
    @Expose
    var year: Int? = null
    /**
     * @return The yday
     */
    /**
     * @param yday The yday
     */
    @Expose
    var yday: Int? = null
    /**
     * @return The hour
     */
    /**
     * @param hour The hour
     */
    @Expose
    var hour: Int? = null
    /**
     * @return The min
     */
    /**
     * @param min The min
     */
    @Expose
    var min: String? = null
    /**
     * @return The sec
     */
    /**
     * @param sec The sec
     */
    @Expose
    var sec: Int? = null
    /**
     * @return The isdst
     */
    /**
     * @param isdst The isdst
     */
    @Expose
    var isdst: String? = null
    /**
     * @return The monthname
     */
    /**
     * @param monthname The monthname
     */
    @Expose
    var monthname: String? = null
    /**
     * @return The monthnameShort
     */
    /**
     * @param monthnameShort The monthname_short
     */
    @SerializedName("monthname_short")
    @Expose
    var monthnameShort: String? = null
    /**
     * @return The weekdayShort
     */
    /**
     * @param weekdayShort The weekday_short
     */
    @SerializedName("weekday_short")
    @Expose
    var weekdayShort: String? = null
    /**
     * @return The weekday
     */
    /**
     * @param weekday The weekday
     */
    @Expose
    var weekday: String? = null
    /**
     * @return The ampm
     */
    /**
     * @param ampm The ampm
     */
    @Expose
    var ampm: String? = null
    /**
     * @return The tzShort
     */
    /**
     * @param tzShort The tz_short
     */
    @SerializedName("tz_short")
    @Expose
    var tzShort: String? = null
    /**
     * @return The tzLong
     */
    /**
     * @param tzLong The tz_long
     */
    @SerializedName("tz_long")
    @Expose
    var tzLong: String? = null

}
