package org.koin.sampleapp.view.weather

import android.os.Bundle
import org.koin.android.contextaware.ContextAwareActivity
import org.koin.android.ext.android.inject
import org.koin.sampleapp.R
import org.koin.sampleapp.di.WeatherModule
import java.util.*

/**
 * Weather View
 */
class WeatherActivity : ContextAwareActivity(), WeatherContract.View {

    override val contextName = WeatherModule.CTX_WEATHER_ACTIVITY

    private val now = Date()

    override val presenter by inject<WeatherContract.Presenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        setSupportActionBar(toolbar)
//
//        weather_main_layout.visibility = View.GONE
//        weather_forecast_layout.visibility = View.GONE
//
//        fab.setOnClickListener { view ->
//            DialogHelper.locationDialog(view, { location ->
//                Snackbar.make(view, "Getting your weather :)", Snackbar.LENGTH_SHORT).show()
//                presenter.getWeather(location)
//            })
//        }
    }

    override fun onResume() {
        super.onResume()
        presenter.view = this
        presenter.start()
    }

    // ContextAwareActivity will drop presenter on onPause

//    override fun displayError(error: Throwable) {
//        Snackbar.make(this.currentFocus, "Got error :( $error", Snackbar.LENGTH_SHORT).show()
//    }
//
//    override fun displayWeather(weather: Weather?, location: String) {
//        if (weather != null) {
//            weather_main_layout.visibility = View.VISIBLE
//            weather_forecast_layout.visibility = View.VISIBLE
//
//            val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)
//            val dateFormat = android.text.format.DateFormat.getDateFormat(applicationContext)
//
//            weather_title.text = getString(R.string.weather_title) + " " + location + "\n" + dateFormat.format(now) + " " + timeFormat.format(now)
//
//            val forecasts = weather.getDailyForecasts()
//
//            if (forecasts.size == 4) {
//                setForecastForToday(forecasts[0], weather_main_text, weather_main_icon)
//                setForecastForDayX(forecasts[1], 1, weather_forecast_day1, weather_icon_day1, weather_temp_day1)
//                setForecastForDayX(forecasts[2], 2, weather_forecast_day2, weather_icon_day2, weather_temp_day2)
//                setForecastForDayX(forecasts[3], 3, weather_forecast_day3, weather_icon_day3, weather_temp_day3)
//            }
//        }
//    }
//
//    private fun setForecastForToday(dayX: DailyForecastModel, forecastDayX: TextView, iconDayX: IconTextView) {
//        forecastDayX.text = "Today : " + dayX.forecastString + "\n" + dayX.temperatureString
//        iconDayX.text = dayX.icon
//    }
//
//    private fun setForecastForDayX(dayX: DailyForecastModel, idx: Int, forecastDayX: TextView, iconDayX: IconTextView, tempDayX: TextView?) {
//        forecastDayX.text = "Day " + idx + ": " + dayX.forecastString
//        if (tempDayX != null) {
//            tempDayX.text = dayX.temperatureString
//        }
//        iconDayX.text = dayX.icon
//    }
}
