package org.koin.sampleapp.view.main

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.property
import org.koin.android.ext.android.setProperty
import org.koin.sampleapp.R
import org.koin.sampleapp.di.WeatherAppProperties.PROPERTY_ADDRESS
import org.koin.sampleapp.di.WeatherAppProperties.PROPERTY_WEATHER_DATE
import org.koin.sampleapp.util.rx.SchedulerProvider
import org.koin.sampleapp.util.rx.with
import org.koin.sampleapp.view.weather.WeatherResultActivity
import java.util.*

/**
 * Weather View
 */
class MainActivity : AppCompatActivity() {

    val mainViewModel by inject<MainViewModel>()
    val scheduler by inject<SchedulerProvider>()
    val defaultAddress by property(PROPERTY_ADDRESS, "")

    val subscription = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchEditText.setText(defaultAddress)

        // Start search weather
        searchButton.setOnClickListener {
            subscription.add(getWeather())
        }
    }

    override fun onResume() {
        super.onResume()
        displayNormal()
    }

    override fun onPause() {
        subscription.dispose()
        super.onPause()
    }

    private fun getWeather(): Disposable {
        return mainViewModel.searchWeather(getSearchText())
                .with(scheduler)
                .doOnSubscribe { displayProgress() }
                .subscribe({
                    displayNormal()
                    onWeatherSuccess()
                }, { error ->
                    displayNormal()
                    onWeatherFailed(error)
                })
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
