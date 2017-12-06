package org.koin.sampleapp.view.weather

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_weather.*
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.property
import org.koin.android.ext.android.releaseContext
import org.koin.android.ext.android.setProperty
import org.koin.sampleapp.R
import org.koin.sampleapp.di.WeatherModule
import org.koin.sampleapp.model.DailyForecastModel
import org.koin.sampleapp.view.detail.WeatherDetailActivity
import org.koin.sampleapp.view.main.PROPERTY_ADDRESS
import java.util.*

/**
 * Weather View
 */
class WeatherResultActivity : AppCompatActivity(), WeatherResultContract.View {

    override val presenter by inject<WeatherResultContract.Presenter>()
    // Get address
    private val address by property<String>(PROPERTY_ADDRESS)
    // get Last date
    private val now by property<Date>(PROPERTY_WEATHER_DATE)

    private lateinit var weatherResultAdapter: WeatherResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        weatherTitle.text = getString(R.string.weather_title).format(address, now)

        weatherList.layoutManager = LinearLayoutManager(this)
        weatherResultAdapter = WeatherResultAdapter(emptyList(), { weatherDetail ->
            // save date & weather detail
            setProperty(PROPERTY_WEATHER_DETAIL, weatherDetail)

            // Launch detail
            startActivity(Intent(this, WeatherDetailActivity::class.java))
        })
        weatherList.itemAnimator = DefaultItemAnimator()
        weatherList.adapter = weatherResultAdapter
    }

    override fun onResume() {
        super.onResume()
        presenter.view = this
        presenter.getWeather(address)
    }

    override fun onPause() {
        presenter.stop()
        releaseContext(WeatherModule.CTX_WEATHER_ACTIVITY)
        super.onPause()
    }

    override fun displayWeather(weatherList: List<DailyForecastModel>) {
        weatherResultAdapter.list = weatherList
        weatherResultAdapter.notifyDataSetChanged()
    }

    override fun displayError(error: Throwable) {
        Snackbar.make(this.currentFocus, "Got error : $error", Snackbar.LENGTH_LONG).show()
    }
}
