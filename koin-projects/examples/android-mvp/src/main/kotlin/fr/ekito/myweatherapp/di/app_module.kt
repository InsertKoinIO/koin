package fr.ekito.myweatherapp.di

import fr.ekito.myweatherapp.data.repository.WeatherRepository
import fr.ekito.myweatherapp.data.repository.WeatherRepositoryImpl
import fr.ekito.myweatherapp.util.rx.ApplicationSchedulerProvider
import fr.ekito.myweatherapp.util.rx.SchedulerProvider
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
    factory {
        SplashPresenter(get(), get()) as SplashContract.Presenter
    }

    // scoped module example
    module("weather") {
        // Presenter for ResultHeader View
        single {
            WeatherHeaderPresenter(get(), get()) as WeatherHeaderContract.Presenter
        }
        // Presenter for ResultList View
        single {
            WeatherListPresenter(get(), get()) as WeatherListContract.Presenter
        }
    }

    // Presenter with injection parameter for Detail View
    factory { (id: String) ->
        DetailPresenter(id, get(), get()) as DetailContract.Presenter
    }

    // Weather Data Repository
    single(createOnStart = true) { WeatherRepositoryImpl(get()) as WeatherRepository }

    // Rx Schedulers
    single { ApplicationSchedulerProvider() as SchedulerProvider }
}

// Gather all app modules
val onlineWeatherApp = listOf(weatherAppModule, remoteDatasourceModule)
val offlineWeatherApp = listOf(weatherAppModule, localAndroidDatasourceModule)