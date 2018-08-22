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
import org.koin.android.viewmodel.experimental.builder.viewModel
import org.koin.experimental.builder.create
import org.koin.dsl.module.module

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
    single<WeatherRepository> { create<WeatherRepositoryImpl>() }

    // Rx Schedulers
    single<SchedulerProvider> { create<ApplicationSchedulerProvider>() }

    // Room Database
    single {
        Room.databaseBuilder(androidContext(), WeatherDatabase::class.java, "weather-db")
            .build()
    }

    // Expose WeatherDAO directly
    single { get<WeatherDatabase>().weatherDAO() }
}

// Gather all app modules
val onlineWeatherApp = listOf(weatherAppModule, remoteDatasourceModule)
val offlineWeatherApp = listOf(weatherAppModule, localAndroidDatasourceModule)