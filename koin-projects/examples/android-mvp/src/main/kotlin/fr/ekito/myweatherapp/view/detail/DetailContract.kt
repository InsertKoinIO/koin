package fr.ekito.myweatherapp.view.detail

import fr.ekito.myweatherapp.domain.entity.DailyForecast
import fr.ekito.myweatherapp.util.mvp.BasePresenter
import fr.ekito.myweatherapp.util.mvp.BaseView

interface DetailContract {
    interface View : BaseView<Presenter> {
        fun showDetail(weather: DailyForecast)
    }

    interface Presenter : BasePresenter<View> {
        fun getDetail(id: String)
    }
}