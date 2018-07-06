package fr.ekito.myweatherapp.di

import android.arch.persistence.room.Room
import fr.ekito.myweatherapp.data.datasource.room.WeatherDatabase
import fr.ekito.myweatherapp.data.repository.WeatherRepository
import fr.ekito.myweatherapp.data.repository.WeatherRepositoryImpl
import fr.ekito.myweatherapp.util.rx.ApplicationSchedulerProvider
import fr.ekito.myweatherapp.util.rx.SchedulerProvider
import fr.ekito.myweatherapp.view.detail.DetailViewModel
import fr.ekito.myweatherapp.view.splash.SplashViewModel
import fr.ekito.myweatherapp.view.weather.WeatherViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

/**
 * App Components
 */
val weatherAppModule = module {

    // ViewModel for Detail view
    viewModel { (id: String) -> DetailViewModel(id, get(), get()) }

    // ViewModel for Search View
    viewModel { SplashViewModel(get(), get()) }

    // WeatherViewModel declaration for Weather View components
    viewModel { WeatherViewModel(get(), get()) }

    // Rx Schedulers
    single { ApplicationSchedulerProvider() as SchedulerProvider }
}

val dataModule = module(createOnStart = true) {
    // Weather Data Repository
    single { WeatherRepositoryImpl(get(), get()) as WeatherRepository }

    // Room Database
    single {
        Room.databaseBuilder(androidContext(), WeatherDatabase::class.java, "weather-db")
            .build()
    }

    // Expose WeatherDAO directly
    single { get<WeatherDatabase>().weatherDAO() }
}

// Gather all app modules
val onlineWeatherApp = listOf(weatherAppModule, dataModule, remoteDatasourceModule)
val offlineWeatherApp = listOf(weatherAppModule, dataModule, localAndroidDatasourceModule)