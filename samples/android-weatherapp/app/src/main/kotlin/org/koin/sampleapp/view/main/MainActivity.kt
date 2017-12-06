package org.koin.sampleapp.view.main

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.property
import org.koin.android.ext.android.releaseContext
import org.koin.android.ext.android.setProperty
import org.koin.sampleapp.R
import org.koin.sampleapp.di.WeatherModule
import org.koin.sampleapp.view.weather.PROPERTY_WEATHER_DATE
import org.koin.sampleapp.view.weather.WeatherResultActivity
import java.util.*

/**
 * Weather View
 */
class MainActivity : AppCompatActivity(), MainContract.View {

    // Presenter
    override val presenter by inject<MainContract.Presenter>()

    // Get last address or default
    private val defaultAddress by property(PROPERTY_ADDRESS, "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchEditText.setText(defaultAddress)

        // Start search weather
        searchButton.setOnClickListener {
            presenter.getWeather(searchText())
        }
    }

    private fun searchText() = searchEditText.text.trim().toString()

    override fun onResume() {
        super.onResume()
        presenter.view = this
    }

    override fun onPause() {
        presenter.stop()
        releaseContext(WeatherModule.CTX_WEATHER_ACTIVITY)
        super.onPause()
    }

    override fun displayNormal() {
        searchProgress.visibility = View.GONE
        searchButton.visibility = View.VISIBLE
    }

    override fun displayProgress() {
        searchProgress.visibility = View.VISIBLE
        searchButton.visibility = View.GONE
    }

    override fun onWeatherSuccess() {
        // save address
        setProperty(PROPERTY_WEATHER_DATE, Date())
        setProperty(PROPERTY_ADDRESS, searchText())

        startActivity(Intent(this, WeatherResultActivity::class.java))
    }

    override fun onWeatherFailed(error: Throwable) {

        Snackbar.make(this.currentFocus, "Got error : $error", Snackbar.LENGTH_LONG).show()
    }
}
