package org.koin.sample.navigation

import android.app.Application
import org.koin.android.ext.koin.useAndroidContext
import org.koin.android.ext.koin.useAndroidLogger
import org.koin.dsl.koinApplication

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        koinApplication {
            useAndroidLogger()
            useAndroidContext(this@MainApplication)
            loadModules(appModule)
        }.start()
    }
}