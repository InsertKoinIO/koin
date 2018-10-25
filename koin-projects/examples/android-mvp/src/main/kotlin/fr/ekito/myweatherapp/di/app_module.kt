package fr.ekito.myweatherapp.di

import fr.ekito.myweatherapp.domain.repository.DailyForecastRepository
import fr.ekito.myweatherapp.domain.repository.DailyForecastRepositoryImpl
import fr.ekito.myweatherapp.util.coroutines.ApplicationSchedulerProvider
import fr.ekito.myweatherapp.util.coroutines.SchedulerProvider
import fr.ekito.myweatherapp.view.detail.DetailContract
import fr.ekito.myweatherapp.view.detail.DetailPresenter
import fr.ekito.myweatherapp.view.splash.SplashContract
import fr.ekito.myweatherapp.view.splash.SplashPresenter
import fr.ekito.myweatherapp.view.weather.WeatherHeaderContract
import fr.ekito.myweatherapp.view.weather.WeatherHeaderPresenter
import fr.ekito.myweatherapp.view.weather.WeatherListContract
import fr.ekito.myweatherapp.view.weather.WeatherListPresenter
import org.koin.dsl.module.module

/**
 * App Components
 */
val weatherAppModule = module {
    // Presenter for Search View
    factory<SplashContract.Presenter> { SplashPresenter(get(), get()) }

    // Presenter for ResultHeader View
    factory<WeatherHeaderContract.Presenter> { WeatherHeaderPresenter(get(), get()) }

    // Presenter for ResultList View
    factory<WeatherListContract.Presenter> { WeatherListPresenter(get(), get()) }

    // Presenter for Detail View
    factory<DetailContract.Presenter> { (id : String) -> DetailPresenter(id, get(), get()) }

    // Weather Data Repository
    single<DailyForecastRepository>(createOnStart = true) { DailyForecastRepositoryImpl(get()) }
    // Rx Schedulers
    single<SchedulerProvider>(createOnStart = true) { ApplicationSchedulerProvider() }
}

// Gather all app modules
val onlineWeatherApp = listOf(weatherAppModule, remoteDataSourceModule)
val offlineWeatherApp = listOf(weatherAppModule, localAndroidDataSourceModule)