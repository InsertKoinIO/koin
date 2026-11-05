package org.koin.sample

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(dataModule, domainModule, uiModule, analyticsModule, dialogModule, authModule, dispatchersModule, dataStoreModule, tokenModule)
        }
    }
}
