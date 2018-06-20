package fr.ekito.myweatherapp.view.weather

import fr.ekito.myweatherapp.util.mvp.BasePresenter
import fr.ekito.myweatherapp.util.mvp.BaseView
import fr.ekito.myweatherapp.view.weather.list.WeatherItem

interface WeatherListContract {
    interface View : BaseView<Presenter> {
        fun showWeatherItemList(newList: List<WeatherItem>)
    }

    interface Presenter : BasePresenter<View> {
        fun getWeatherList()
    }
}