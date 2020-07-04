package org.koin.sample.androidx.compose

import android.app.Application
import org.koin.core.context.startKoin
import org.koin.sample.androidx.compose.di.appModule

class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModule)
        }
    }
}