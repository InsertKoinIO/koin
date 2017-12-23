package org.koin.sampleapp.view.detail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_weather_detail.*
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.property
import org.koin.sampleapp.R
import org.koin.sampleapp.di.WeatherAppProperties.PROPERTY_ADDRESS
import org.koin.sampleapp.di.WeatherAppProperties.PROPERTY_WEATHER_DATE
import org.koin.sampleapp.model.DailyForecastModel
import org.koin.sampleapp.util.rx.SchedulerProvider
import org.koin.sampleapp.util.rx.with
import java.util.*

/**
 * Weather Detail View
 */
class WeatherDetailActivity : AppCompatActivity() {

    // Get all needed data
    private val address by property<String>(PROPERTY_ADDRESS)
    private val now by property<Date>(PROPERTY_WEATHER_DATE)

    val detailViewModel: WeatherDetailViewModel by inject()
    val scheduler by inject<SchedulerProvider>()

    val subscription = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_detail)

        weatherTitle.text = getString(R.string.weather_title).format(address, now)
    }

    override fun onResume() {
        super.onResume()
        subscription.add(detailViewModel.getDetail()
                .with(scheduler)
                .subscribe({ displayDetail(it) }, { System.err.println(it) })
        )
    }

    override fun onPause() {
        subscription.clear()
        super.onPause()
    }

    fun displayDetail(weather: DailyForecastModel) {
        weatherItemIcon.text = weather.icon
        weatherItemForecast.text = weather.forecastString
        weatherItemTemp.text = weather.temperatureString
    }
}
