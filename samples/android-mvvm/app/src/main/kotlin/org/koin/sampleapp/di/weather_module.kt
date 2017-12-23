package org.koin.sampleapp.di

import org.koin.dsl.module.applicationContext
import org.koin.sampleapp.repository.WeatherRepository
import org.koin.sampleapp.repository.WeatherRepositoryImpl
import org.koin.sampleapp.view.main.MainViewModel
import org.koin.sampleapp.view.weather.WeatherResultContract
import org.koin.sampleapp.view.weather.WeatherResultPresenter

val weatherModule = applicationContext {

    factory { MainViewModel(get()) }

    factory { WeatherResultPresenter(get(), get()) as WeatherResultContract.Presenter }

    provide { WeatherRepositoryImpl(get()) } bind WeatherRepository::class
}

object WeatherAppProperties {
    val PROPERTY_ADDRESS : String = "PROPERTY_ADDRESS"
}