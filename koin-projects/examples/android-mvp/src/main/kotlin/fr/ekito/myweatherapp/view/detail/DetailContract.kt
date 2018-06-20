package fr.ekito.myweatherapp.view.detail

import fr.ekito.myweatherapp.domain.DailyForecastModel
import fr.ekito.myweatherapp.util.mvp.BasePresenter
import fr.ekito.myweatherapp.util.mvp.BaseView

interface DetailContract {
    interface View : BaseView<Presenter> {
        fun showDetail(weather: DailyForecastModel)
    }

    interface Presenter : BasePresenter<View> {
        fun getDetail(id: String)
    }
}