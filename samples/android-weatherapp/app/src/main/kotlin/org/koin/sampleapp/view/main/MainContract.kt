package org.koin.sampleapp.view.main

import org.koin.sampleapp.util.mvp.BasePresenter
import org.koin.sampleapp.util.mvp.BaseView

/**
 * Weather MVP Contract
 */
interface MainContract {
    interface View : BaseView<Presenter> {
        fun onWeatherSuccess()
        fun onWeatherFailed(error: Throwable)
    }

    interface Presenter : BasePresenter<View> {
        fun getWeather(address: String)
    }
}

const val PROPERTY_ADDRESS: String = "ADDRESS"