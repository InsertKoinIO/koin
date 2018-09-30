package org.koin.sample.navigation

import android.app.Application
import org.koin.android.ext.android.startKoin
import org.koin.android.logger.AndroidLogger

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin(this, listOf(appModule), logger = AndroidLogger(showDebug = true))
    }
}