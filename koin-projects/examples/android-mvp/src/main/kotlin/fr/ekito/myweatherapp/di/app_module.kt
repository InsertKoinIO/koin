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
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

/**
 * App Components
 */
val weatherAppModule = module {

    // Presenter for Search View
    factory<SplashContract.Presenter> { SplashPresenter(get(), get()) }

    // scoped module example
    module(WeatherActivity::class.moduleName) {
        // Presenter for ResultHeader View
        single<WeatherHeaderContract.Presenter> { WeatherHeaderPresenter(get(), get()) }

        // Presenter for ResultList View
        single<WeatherListContract.Presenter> { WeatherListPresenter(get(), get()) }
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

class Shared

object Memory {
    lateinit var wr: WeakReference<Shared> //(Shared(), ReferenceQueue())

    fun getShared(): Shared {
        if (!this::wr.isInitialized) {
            create()
        }
        if (wr.isEnqueued || wr.get() == null) {
            create()
        }
        val shared = wr.get() ?: error("Can't be null")
        println(" SHARED -> $shared")
        return shared
    }

    fun release(){
        println("UNSUB")
        wr.enqueue()
    }

    private fun create() {
        println("CREATE")
        wr = WeakReference(Shared(), ReferenceQueue())
    }


}
