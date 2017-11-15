package org.koin.sampleapp.view.main

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.contextaware.ContextAwareActivity
import org.koin.android.ext.android.bindProperty
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.property
import org.koin.sampleapp.R
import org.koin.sampleapp.di.WeatherModule
import org.koin.sampleapp.view.weather.WeatherResultActivity

/**
 * Weather View
 */
class MainActivity : ContextAwareActivity(), MainContract.View {

    override val contextName = WeatherModule.CTX_WEATHER_ACTIVITY

    override val presenter by inject<MainContract.Presenter>()

    private val defaultAddress by property(PROPERTY_ADDRESS, "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchEditText.setText(defaultAddress)
        searchEditText.setOnFocusChangeListener { _, _ ->
            bindProperty(PROPERTY_ADDRESS, searchText())
        }

        searchButton.setOnClickListener { presenter.getWeather(searchText()) }
    }

    private fun searchText() = searchEditText.text.trim().toString()

    override fun onResume() {
        super.onResume()
        presenter.view = this
        presenter.start()
    }

    override fun onPause() {
        presenter.stop()
        super.onPause()
    }

    override fun onWeatherSuccess() {
        startActivity(Intent(this, WeatherResultActivity::class.java))
    }

    override fun onWeatherFailed(error: Throwable) {
        Snackbar.make(this.currentFocus, "Got error : $error", Snackbar.LENGTH_LONG).show()
    }
}
