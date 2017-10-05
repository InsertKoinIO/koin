package fr.ekito.myweatherapp.di

import fr.ekito.myweatherapp.util.TestSchedulerProvider
import koin.sampleapp.util.rx.SchedulerProvider
import org.koin.android.AndroidModule
import org.koin.dsl.context.Context

class TestWebServicesModule : AndroidModule() {

    override fun context(): Context {
        return declareContext {
            // provided components
            provide { TestSchedulerProvider() } bind (SchedulerProvider::class)
        }
    }

    companion object {
        const val SERVER_URL = "https://my-weather-api.herokuapp.com/"
    }
}