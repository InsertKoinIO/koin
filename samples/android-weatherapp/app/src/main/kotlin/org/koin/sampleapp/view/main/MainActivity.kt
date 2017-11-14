package org.koin.sampleapp.view.main

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.TextView
import com.joanzapata.iconify.widget.IconTextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.koin.android.contextaware.ContextAwareActivity
import org.koin.android.ext.android.inject
import org.koin.sampleapp.R
import org.koin.sampleapp.di.WeatherModule
import org.koin.sampleapp.repository.json.getDailyForecasts
import org.koin.sampleapp.repository.json.weather.Weather
import org.koin.sampleapp.util.DialogHelper
import org.koin.sampleapp.model.DailyForecastModel
import java.util.*

/**
 * Weather View
 */
class MainActivity : ContextAwareActivity(), MainContract.View {

    override val contextName = WeatherModule.CTX_WEATHER_ACTIVITY

    override val presenter by inject<MainContract.Presenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        setSupportActionBar(toolbar)
    }

    override fun onResume() {
        super.onResume()
        presenter.view = this
        presenter.start()
    }

    override fun onPause() {
        super.onPause()
        presenter.stop()
    }
}
