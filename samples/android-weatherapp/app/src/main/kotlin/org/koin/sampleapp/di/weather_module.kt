package org.koin.sampleapp.di

import org.koin.dsl.module.applicationContext
import org.koin.sampleapp.di.Context.CTX_MAIN_ACTIVITY
import org.koin.sampleapp.di.Context.CTX_WEATHER_ACTIVITY
import org.koin.sampleapp.repository.WeatherRepository
import org.koin.sampleapp.repository.WeatherRepositoryImpl
import org.koin.sampleapp.view.main.MainContract
import org.koin.sampleapp.view.main.MainPresenter
import org.koin.sampleapp.view.weather.WeatherResultContract
import org.koin.sampleapp.view.weather.WeatherResultPresenter

val WeatherModule = applicationContext {
    context(CTX_MAIN_ACTIVITY) {
        provide { MainPresenter(get(), get()) as MainContract.Presenter }
    }
    context(CTX_WEATHER_ACTIVITY) {
        provide { WeatherResultPresenter(get(), get()) as WeatherResultContract.Presenter }
    }

    provide { WeatherRepositoryImpl(get()) as WeatherRepository }
}

object Context {
    const val CTX_MAIN_ACTIVITY = "MainActivity"
    const val CTX_WEATHER_ACTIVITY = "WeatherResultActivity"
}
