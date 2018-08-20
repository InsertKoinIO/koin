package fr.ekito.myweatherapp.di

import fr.ekito.myweatherapp.data.repository.WeatherRepository
import fr.ekito.myweatherapp.data.repository.WeatherRepositoryImpl
import fr.ekito.myweatherapp.util.rx.ApplicationSchedulerProvider
import fr.ekito.myweatherapp.util.rx.SchedulerProvider
import fr.ekito.myweatherapp.view.detail.DetailContract
import fr.ekito.myweatherapp.view.detail.DetailPresenter
import fr.ekito.myweatherapp.view.splash.SplashContract
import fr.ekito.myweatherapp.view.splash.SplashPresenter
import fr.ekito.myweatherapp.view.weather.*
import org.koin.dsl.module.module
import org.koin.dsl.path.moduleName

/**
 * App Components
 */
val weatherAppModule = module {

    // Presenter for Search View
    factory<SplashContract.Presenter> { create<SplashPresenter>() }

    // scoped module example
    module(WeatherActivity::class.moduleName) {
        // Presenter for ResultHeader View
        single<WeatherHeaderContract.Presenter> { create<WeatherHeaderPresenter>() }

        // Presenter for ResultList View
        single<WeatherListContract.Presenter> { create<WeatherListPresenter>() }
    }

    // Presenter with injection parameter for Detail View
    factory<DetailContract.Presenter> { (id: String) -> DetailPresenter(id, get(), get()) }

    // Weather Data Repository
    single<WeatherRepository>(createOnStart = true) { create<WeatherRepositoryImpl>() }

    // Rx Schedulers
    single<SchedulerProvider> { ApplicationSchedulerProvider() }
}

// Gather all app modules
val onlineWeatherApp = listOf(weatherAppModule, remoteDatasourceModule)
val offlineWeatherApp = listOf(weatherAppModule, localAndroidDatasourceModule)