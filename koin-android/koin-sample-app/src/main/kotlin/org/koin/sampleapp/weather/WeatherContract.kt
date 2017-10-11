package org.koin.sampleapp.weather

import org.koin.sampleapp.mvp.BasePresenter
import org.koin.sampleapp.mvp.BaseView
import org.koin.sampleapp.repository.json.weather.Weather

/**
 * Weather MVP Contract
 */
interface WeatherContract {
    interface View : BaseView<Presenter> {

        fun displayWeather(weather: Weather?, location: String)
        fun displayError(error: Throwable)
    }

    interface Presenter : BasePresenter<View> {

        fun getWeather(location: String)

    }
}