package org.koin.sampleapp.view.detail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_weather_detail.*
import org.koin.android.ext.android.property
import org.koin.sampleapp.R
import org.koin.sampleapp.di.WeatherAppProperties.PROPERTY_ADDRESS
import org.koin.sampleapp.model.DailyForecastModel
import org.koin.sampleapp.view.weather.PROPERTY_WEATHER_DATE
import org.koin.sampleapp.view.weather.PROPERTY_WEATHER_DETAIL
import java.util.*

/**
 * Weather Detail View
 */
class WeatherDetailActivity : AppCompatActivity() {

    // Get all needed data
    private val address by property<String>(PROPERTY_ADDRESS)
    private val now by property<Date>(PROPERTY_WEATHER_DATE)
    private val weather by property<DailyForecastModel>(PROPERTY_WEATHER_DETAIL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_detail)

        weatherTitle.text = getString(R.string.weather_title).format(address, now)
        weatherItemIcon.text = weather.icon
        weatherItemForecast.text = weather.forecastString
        weatherItemTemp.text = weather.temperatureString
    }
}
