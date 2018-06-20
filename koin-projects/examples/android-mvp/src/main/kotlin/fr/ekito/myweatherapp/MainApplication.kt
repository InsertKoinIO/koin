package fr.ekito.myweatherapp

import android.app.Application
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.WeathericonsModule
import com.squareup.leakcanary.LeakCanary
import fr.ekito.myweatherapp.di.offlineWeatherApp
import org.koin.android.ext.android.startKoin

/**
 * Main Application
 */
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)

        // start Koin context
        startKoin(this, offlineWeatherApp, createEagerInstances = true)

        Iconify
            .with(WeathericonsModule())
    }
}
