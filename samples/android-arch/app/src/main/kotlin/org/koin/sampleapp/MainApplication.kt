package org.koin.sampleapp

import android.support.multidex.MultiDexApplication
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.WeathericonsModule
import org.koin.android.ext.android.startKoin
import org.koin.sampleapp.di.weatherAppModules

/**
 * Main Application
 */
class MainApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        // start Koin context
        startKoin(this, weatherAppModules)

        Iconify.with(WeathericonsModule())
    }
}
