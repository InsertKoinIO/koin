package koin.sampleapp.weather

import koin.sampleapp.BasePresenter
import koin.sampleapp.BaseView
import koin.sampleapp.json.weather.Weather

/**
 * Created by arnaud on 25/09/2017.
 */
interface WeatherContract {
    interface View : BaseView<Presenter> {

        fun displayWeather(weather: Weather?, location: String)
    }

    interface Presenter : BasePresenter<View> {

        fun getWeather(location: String)

    }
}