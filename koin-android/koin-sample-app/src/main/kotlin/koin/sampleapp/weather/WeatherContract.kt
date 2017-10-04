package koin.sampleapp.weather

import koin.sampleapp.BasePresenter
import koin.sampleapp.BaseView
import koin.sampleapp.service.json.weather.Weather

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