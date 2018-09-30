package fr.ekito.myweatherapp.view.weather

import fr.ekito.myweatherapp.domain.entity.DailyForecast
import fr.ekito.myweatherapp.util.mvp.BasePresenter
import fr.ekito.myweatherapp.util.mvp.BaseView

interface WeatherHeaderContract {
    interface View : BaseView<Presenter> {
        fun showWeather(location : String, weather: DailyForecast)
        fun showLocationSearchSucceed(location: String)
        fun showLocationSearchFailed(location: String, error: Throwable)
    }

    interface Presenter : BasePresenter<View> {
        fun getWeatherOfTheDay()
        fun loadNewLocation(location: String)
    }
}