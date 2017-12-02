package org.koin.sampleapp

import android.app.Application
import android.content.pm.ApplicationInfo
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.WeathericonsModule
import org.koin.Koin
import org.koin.android.ext.android.startKoin
import org.koin.android.logger.AndroidLogger
import org.koin.sampleapp.di.weatherAppModules

/**
 * Main Application
 */
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Display some logs
        val isDebug = (0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE)
        if (isDebug) {
            Koin.logger = AndroidLogger()
        }

        // start Koin context
        startKoin(weatherAppModules())

        Iconify.with(WeathericonsModule())
    }
}
