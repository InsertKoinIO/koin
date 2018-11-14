package fr.ekito.myweatherapp

import android.app.Application
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.WeathericonsModule
import com.squareup.leakcanary.LeakCanary
import fr.ekito.myweatherapp.di.offlineWeatherApp
import org.koin.android.ext.koin.useAndroidContext
import org.koin.android.ext.koin.useAndroidLogger
import org.koin.dsl.koinApplication

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
        koinApplication {
            useAndroidLogger()
            useAndroidContext(this@MainApplication)
            loadModules(offlineWeatherApp)
        }.start()

        Iconify
            .with(WeathericonsModule())
    }
}
