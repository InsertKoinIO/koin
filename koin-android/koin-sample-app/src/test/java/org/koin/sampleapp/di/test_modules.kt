package org.koin.sampleapp.di

import org.koin.sampleapp.util.TestSchedulerProvider
import org.koin.sampleapp.rx.SchedulerProvider
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