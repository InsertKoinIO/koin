package org.koin.sampleapp.view.main

import org.koin.sampleapp.util.mvp.BasePresenter
import org.koin.sampleapp.util.mvp.BaseView
import org.koin.sampleapp.repository.json.weather.Weather

/**
 * Weather MVP Contract
 */
interface MainContract {
    interface View : BaseView<Presenter> {

    }

    interface Presenter : BasePresenter<View> {

    }
}