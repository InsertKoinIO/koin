package org.koin.sampleapp.view.weather

import org.koin.sampleapp.model.DailyForecastModel
import org.koin.sampleapp.util.mvp.BasePresenter
import org.koin.sampleapp.util.mvp.BaseView

/**
 * Weather MVP Contract
 */
interface WeatherResultContract {
    interface View : BaseView<Presenter> {
        fun displayWeather(weatherList: List<DailyForecastModel>)
        fun displayError(error: Throwable)
    }

    interface Presenter : BasePresenter<View> {
        fun getWeather(address: String)
    }
}

const val PROPERTY_WEATHER_DETAIL = "WEATHER_DETAIL"
const val PROPERTY_WEATHER_DATE = "WEATHER_DATE"

