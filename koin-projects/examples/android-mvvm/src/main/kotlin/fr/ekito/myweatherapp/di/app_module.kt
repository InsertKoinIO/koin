package fr.ekito.myweatherapp.di

import fr.ekito.myweatherapp.domain.entity.UserSession
import fr.ekito.myweatherapp.domain.repository.DailyForecastRepository
import fr.ekito.myweatherapp.domain.repository.DailyForecastRepositoryImpl
import fr.ekito.myweatherapp.util.coroutines.ApplicationSchedulerProvider
import fr.ekito.myweatherapp.util.coroutines.SchedulerProvider
import fr.ekito.myweatherapp.view.detail.DetailViewModel
import fr.ekito.myweatherapp.view.splash.SplashViewModel
import fr.ekito.myweatherapp.view.weather.WeatherViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

/**
 * App Components
 */
val weatherAppModule = module {
    // SplashViewModel for Splash View
    viewModel { SplashViewModel(get(), get()) }

    viewModel { WeatherViewModel(get(), get(), get()) }

    viewModel { (id: String) -> DetailViewModel(id, get(), get()) }

    scope("session") { UserSession() }

    // Weather Data Repository
    single<DailyForecastRepository>(createOnStart = true) { DailyForecastRepositoryImpl(get()) }
    // Rx Schedulers
    single<SchedulerProvider>(createOnStart = true) { ApplicationSchedulerProvider() }
}

// Gather all app modules
val onlineWeatherApp = listOf(weatherAppModule, remoteDataSourceModule)
val offlineWeatherApp = listOf(weatherAppModule, localAndroidDataSourceModule)
val roomWeatherApp = listOf(weatherAppModule, localAndroidDataSourceModule, roomDataSourceModule)