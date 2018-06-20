package fr.ekito.myweatherapp.view.splash

import fr.ekito.myweatherapp.util.mvp.BasePresenter
import fr.ekito.myweatherapp.util.mvp.BaseView

/**
 * Weather MVP Contract
 */
interface SplashContract {
    interface View : BaseView<Presenter> {
        fun showIsLoaded()
        fun showIsLoading()
    }

    interface Presenter : BasePresenter<View> {
        fun getLastWeather()
    }
}