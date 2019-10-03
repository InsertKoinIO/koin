package org.koin.sample.androidx

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.sample.androidx.di.appModule
import org.koin.sample.androidx.di.mvpModule
import org.koin.sample.androidx.di.mvvmModule
import org.koin.sample.androidx.di.scopeModule

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MainApplication)
            androidFileProperties()
            modules(listOf(appModule, mvpModule, mvvmModule, scopeModule))
        }
    }
}