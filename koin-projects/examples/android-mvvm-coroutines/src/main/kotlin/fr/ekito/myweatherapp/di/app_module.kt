package fr.ekito.myweatherapp.di

import fr.ekito.myweatherapp.domain.repository.WeatherRepository
import fr.ekito.myweatherapp.domain.repository.WeatherRepositoryImpl
import fr.ekito.myweatherapp.util.coroutines.ApplicationSchedulerProvider
import fr.ekito.myweatherapp.util.coroutines.SchedulerProvider
import fr.ekito.myweatherapp.view.detail.DetailViewModel
import fr.ekito.myweatherapp.view.splash.SplashViewModel
import fr.ekito.myweatherapp.view.weather.WeatherViewModel
import org.koin.android.viewmodel.experimental.builder.viewModel
import org.koin.dsl.module
import org.koin.experimental.builder.singleBy

/**
 * App Components
 */
val weatherAppModule = module {

    viewModel<DetailViewModel>()

    // ViewModel for Search View
    viewModel<SplashViewModel>()

    // WeatherViewModel declaration for Weather View components
    viewModel<WeatherViewModel>()

    // Weather Data Repository
    singleBy<WeatherRepository, WeatherRepositoryImpl>(createOnStart = true)
    // Schedulers
    singleBy<SchedulerProvider, ApplicationSchedulerProvider>(createOnStart = true)
}

// Gather all app modules
val onlineWeatherApp = listOf(weatherAppModule, remoteDatasourceModule)
val offlineWeatherApp = listOf(weatherAppModule, localAndroidDatasourceModule)