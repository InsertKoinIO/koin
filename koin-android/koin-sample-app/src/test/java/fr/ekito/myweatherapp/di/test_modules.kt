package fr.ekito.myweatherapp.di

import fr.ekito.myweatherapp.util.TestSchedulerProvider
import koin.sampleapp.util.rx.SchedulerProvider
import org.koin.android.AndroidModule

class RxTestModule : AndroidModule() {

    override fun context() = applicationContext {
        // provided components
        provide { TestSchedulerProvider() } bind (SchedulerProvider::class)
    }

    companion object {
        const val SERVER_URL = "https://my-weather-api.herokuapp.com/"
    }
}