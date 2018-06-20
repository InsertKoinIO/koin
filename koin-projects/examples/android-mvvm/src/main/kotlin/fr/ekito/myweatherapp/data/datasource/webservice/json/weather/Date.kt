package fr.ekito.myweatherapp.data.datasource.webservice.json.weather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Date(
        @Expose var epoch: String? = null,
        @Expose var pretty: String? = null,
        @Expose var day: Int? = null,
        @Expose var month: Int? = null,
        @Expose var year: Int? = null,
        @Expose var yday: Int? = null,
        @Expose var hour: Int? = null,
        @Expose var min: String? = null,
        @Expose var sec: Int? = null,
        @Expose var isdst: String? = null,
        @Expose var monthname: String? = null,
        @SerializedName("monthname_short")
        @Expose var monthnameShort: String? = null,
        @SerializedName("weekday_short")
        @Expose var weekdayShort: String? = null,
        @Expose var weekday: String? = null,
        @Expose var ampm: String? = null,
        @SerializedName("tz_short")
        @Expose var tzShort: String? = null,
        @SerializedName("tz_long")
        @Expose var tzLong: String? = null
)
