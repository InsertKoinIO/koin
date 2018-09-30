package fr.ekito.myweatherapp.data.json

import java.util.*

data class Avewind(
    val mph: Int? = null,
    val kph: Int? = null,
    val dir: String? = null,
    val degrees: Int? = null
)

data class Date(
    val epoch: String? = null,
    val pretty: String? = null,
    val day: Int? = null,
    val month: Int? = null,
    val year: Int? = null,
    val yday: Int? = null,
    val hour: Int? = null,
    val min: String? = null,
    val sec: Int? = null,
    val isdst: String? = null,
    val monthname_short: String? = null,
    val weekday_short: String? = null,
    val weekday: String? = null,
    val ampm: String? = null,
    val tz_short: String? = null,
    val tz_long: String? = null
)

data class Features(val forecast: Int? = null)

data class Forecast(
    val txtForecast: TxtForecast? = null,
    val simpleforecast: Simpleforecast? = null
)

data class Forecastday(
    val period: Int? = null,
    val icon: String? = null,
    val iconUrl: String? = null,
    val title: String? = null,
    val fcttext: String? = null,
    val fcttextMetric: String? = null,
    val pop: String? = null
)

data class Forecastday_(
    val date: Date? = null,
    val period: Int? = null,
    val high: High? = null,
    val low: Low? = null,
    val conditions: String? = null,
    val icon: String? = null,
    val iconUrl: String? = null,
    val skyicon: String? = null,
    val pop: Int? = null,
    val qpf_allday: QpfAllday? = null,
    val qpf_day: QpfDay? = null,
    val qpf_night: QpfNight? = null,
    val snow_allday: SnowAllday? = null,
    val snow_day: SnowDay? = null,
    val snow_night: SnowNight? = null,
    val maxwind: Maxwind? = null,
    val avewind: Avewind? = null,
    val avehumidity: Int? = null,
    val maxhumidity: Int? = null,
    val minhumidity: Int? = null
)

data class High(
    val fahrenheit: String? = null,
    val celsius: String? = null
)

data class Low(
    val fahrenheit: String? = null,
    val celsius: String? = null
)

data class Maxwind(
    val mph: Int? = null,
    val kph: Int? = null,
    val dir: String? = null,
    val degrees: Int? = null
)

data class QpfAllday(
    val `in`: Double? = null,
    val mm: Int? = null
)

data class QpfDay(
    val `in`: Double? = null,
    val mm: Int? = null
)

data class QpfNight(
    val `in`: Double? = null,
    val mm: Int? = null
)

data class Response(
    val version: String? = null,
    val termsofService: String? = null,
    val features: Features? = null
)

data class Simpleforecast(val forecastday: List<Forecastday_> = ArrayList())

data class SnowAllday(
    val `in`: Double? = null,
    val cm: Double? = null
)

data class SnowDay(
    val `in`: Double? = null,
    val cm: Double? = null
)

data class SnowNight(
    val `in`: Double? = null,
    val cm: Double? = null
)

data class TxtForecast(
    val date: String? = null,
    val forecastday: List<Forecastday> = ArrayList()
)

data class Weather(
    val response: Response? = null,
    val forecast: Forecast? = null
)