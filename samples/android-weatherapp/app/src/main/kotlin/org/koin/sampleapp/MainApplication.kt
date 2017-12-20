package org.koin.sampleapp

import android.app.Application
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.WeathericonsModule
import org.koin.android.ext.android.startKoin
import org.koin.sampleapp.di.weatherAppModules

/**
 * Main Application
 */
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // start Koin context
        startKoin(this, weatherAppModules)

        Iconify.with(WeathericonsModule())
    }
}
