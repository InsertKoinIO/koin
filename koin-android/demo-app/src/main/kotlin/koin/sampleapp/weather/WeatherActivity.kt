package koin.sampleapp.weather

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.joanzapata.iconify.widget.IconTextView
import fr.ekito.myweatherapp.DailyForecastModel
import fr.ekito.myweatherapp.DialogHelper
import koin.sampleapp.R
import koin.sampleapp.service.json.getDailyForecasts
import koin.sampleapp.service.json.weather.Weather
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.koin.android.ext.android.app.inject
import org.koin.android.ext.android.app.release
import java.util.*

/**
 * Weather View
 */
class WeatherActivity() : AppCompatActivity(), WeatherContract.View {

    private val now = Date()

    override val presenter: WeatherContract.Presenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        weather_main_layout.visibility = View.GONE
        weather_forecast_layout.visibility = View.GONE

        fab.setOnClickListener { view ->
            DialogHelper.locationDialog(view, { location ->
                Snackbar.make(view, "Getting your weather :)", Snackbar.LENGTH_SHORT).show()
                presenter.getWeather(location)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.view = this
        presenter.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.stop()
        release(this)
    }

    override fun displayError(error: Throwable) {
        Snackbar.make(this.currentFocus, "Got error :( $error", Snackbar.LENGTH_SHORT).show()
    }

    override fun displayWeather(weather: Weather?, location: String) {
        if (weather != null) {
            weather_main_layout.visibility = View.VISIBLE
            weather_forecast_layout.visibility = View.VISIBLE

            val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)
            val dateFormat = android.text.format.DateFormat.getDateFormat(applicationContext)

            weather_title.text = getString(R.string.weather_title) + " " + location + "\n" + dateFormat.format(now) + " " + timeFormat.format(now)

            val forecasts = weather.getDailyForecasts()

            if (forecasts.size == 4) {
                setForecastForToday(forecasts[0], weather_main_text, weather_main_icon)
                setForecastForDayX(forecasts[1], 1, weather_forecast_day1, weather_icon_day1, weather_temp_day1)
                setForecastForDayX(forecasts[2], 2, weather_forecast_day2, weather_icon_day2, weather_temp_day2)
                setForecastForDayX(forecasts[3], 3, weather_forecast_day3, weather_icon_day3, weather_temp_day3)
            }
        }
    }

    private fun setForecastForToday(dayX: DailyForecastModel, forecastDayX: TextView, iconDayX: IconTextView) {
        forecastDayX.text = "Today : " + dayX.forecastString + "\n" + dayX.temperatureString
        iconDayX.text = dayX.icon
    }

    private fun setForecastForDayX(dayX: DailyForecastModel, idx: Int, forecastDayX: TextView, iconDayX: IconTextView, tempDayX: TextView?) {
        forecastDayX.text = "Day " + idx + ": " + dayX.forecastString
        if (tempDayX != null) {
            tempDayX.text = dayX.temperatureString
        }
        iconDayX.text = dayX.icon
    }
}
