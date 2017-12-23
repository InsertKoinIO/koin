package org.koin.sampleapp.view.weather

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_weather.*
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.property
import org.koin.sampleapp.R
import org.koin.sampleapp.di.WeatherAppProperties.PROPERTY_ADDRESS
import org.koin.sampleapp.di.WeatherAppProperties.PROPERTY_WEATHER_DATE
import org.koin.sampleapp.model.DailyForecastModel
import org.koin.sampleapp.util.rx.SchedulerProvider
import org.koin.sampleapp.util.rx.with
import org.koin.sampleapp.view.detail.WeatherDetailActivity
import java.util.*

/**
 * Weather View
 */
class WeatherResultActivity : AppCompatActivity() {

    val resultViewModel by inject<WeatherResultViewModel>()
    val scheduler by inject<SchedulerProvider>()

    // Get address
    private val address by property<String>(PROPERTY_ADDRESS)
    // get Last date
    private val now by property<Date>(PROPERTY_WEATHER_DATE)

    val subscription = CompositeDisposable()

    private lateinit var weatherResultAdapter: WeatherResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        weatherTitle.text = getString(R.string.weather_title).format(address, now)

        weatherList.layoutManager = LinearLayoutManager(this)
        weatherResultAdapter = WeatherResultAdapter(emptyList(), { weatherDetail ->
            // save date & weather detail
            onDetailClicked(weatherDetail)
        })
        weatherList.itemAnimator = DefaultItemAnimator()
        weatherList.adapter = weatherResultAdapter
    }


    private fun getWeatherList() = resultViewModel.getWeatherList(address)
            .with(scheduler)
            .doOnSubscribe { println("sub -> getWeatherList") }
            .subscribe({ displayWeather(it) }, { displayError(it) })

    override fun onResume() {
        super.onResume()
        subscription.add(getWeatherList())
    }

    override fun onPause() {
        subscription.dispose()
        super.onPause()
    }

    fun displayWeather(weatherList: List<DailyForecastModel>) {
        weatherResultAdapter.list = weatherList
        weatherResultAdapter.notifyDataSetChanged()
    }

    fun onDetailClicked(weatherDetail: DailyForecastModel) {
        resultViewModel.selectDetail(weatherDetail)
                .with(scheduler)
                .subscribe({
                    onDetailSaved()
                }, { displayError(it) })
    }

    fun onDetailSaved() {
        startActivity(Intent(this, WeatherDetailActivity::class.java))
    }

    fun displayError(error: Throwable) {
        Snackbar.make(this.currentFocus, "Got error : $error", Snackbar.LENGTH_LONG).show()
    }
}
