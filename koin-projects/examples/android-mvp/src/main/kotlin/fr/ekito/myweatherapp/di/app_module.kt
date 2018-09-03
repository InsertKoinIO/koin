package fr.ekito.myweatherapp.di

import fr.ekito.myweatherapp.data.repository.WeatherRepository
import fr.ekito.myweatherapp.data.repository.WeatherRepositoryImpl
import fr.ekito.myweatherapp.domain.UserSession
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
    factory<SplashContract.Presenter> { SplashPresenter(get(), get()) }

    // scoped module example
    module("WeatherActivity") {

        // Shared session
        // shared with WeatherActivity WeatherHeaderFragment & WeatherHeaderPresenter
        scope { UserSession() }

        // Presenter for ResultHeader View
        factory<WeatherHeaderContract.Presenter> {
            WeatherHeaderPresenter(
                get(),
                get(),
                get(scope = getScope("session"))
            )
        }

        // Presenter for ResultList View
        factory<WeatherListContract.Presenter> { WeatherListPresenter(get(), get()) }
    }

    // Presenter with injection parameter for Detail View
    factory<DetailContract.Presenter> { (id: String) -> DetailPresenter(id, get(), get()) }

    // Weather Data Repository
    single<WeatherRepository>(createOnStart = true) { WeatherRepositoryImpl(get()) }

    // Rx Schedulers
    single<SchedulerProvider> { ApplicationSchedulerProvider() }
}

// Gather all app modules
val onlineWeatherApp = listOf(weatherAppModule, remoteDatasourceModule)
val offlineWeatherApp = listOf(weatherAppModule, localAndroidDatasourceModule)
