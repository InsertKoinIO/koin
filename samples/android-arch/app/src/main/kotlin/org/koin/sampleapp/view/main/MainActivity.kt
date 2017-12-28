package org.koin.sampleapp.view.main

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.property
import org.koin.android.ext.android.setProperty
import org.koin.sampleapp.R
import org.koin.sampleapp.di.WeatherAppProperties.PROPERTY_ADDRESS
import org.koin.sampleapp.di.WeatherAppProperties.PROPERTY_WEATHER_DATE
import org.koin.sampleapp.view.weather.WeatherResultActivity
import java.util.*


/**
 * Weather View
 */
class MainActivity : AppCompatActivity() {

    val defaultAddress by property(PROPERTY_ADDRESS, "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchEditText.setText(defaultAddress)

        val model = ViewModelProviders.of(this).get(MainViewModel::class.java)

        // Start search weather
        searchButton.setOnClickListener {
            displayProgress()
            model.searchWeather(getSearchText())
        }

        model.searchOk.observe(this, android.arch.lifecycle.Observer {
            println("got $it")
            if (it != null) {
                onWeatherSuccess()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        displayNormal()
    }

    fun getSearchText() = searchEditText.text.trim().toString()

    fun displayNormal() {
        searchProgress.visibility = View.GONE
        searchButton.visibility = View.VISIBLE
    }

    fun displayProgress() {
        searchProgress.visibility = View.VISIBLE
        searchButton.visibility = View.GONE
    }

    fun onWeatherSuccess() {
        // save properties
        setProperty(PROPERTY_WEATHER_DATE, Date())
        setProperty(PROPERTY_ADDRESS, getSearchText())

        startActivity(Intent(this, WeatherResultActivity::class.java))
    }

    fun onWeatherFailed(error: Throwable) {
        Snackbar.make(this.currentFocus, "Got error : $error", Snackbar.LENGTH_LONG).show()
    }
}
