package fr.ekito.myweatherapp

import android.app.Application
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.WeathericonsModule
import fr.ekito.myweatherapp.di.offlineWeatherApp
import org.koin.android.ext.koin.useAndroidContext
import org.koin.android.ext.koin.useAndroidLogger
import org.koin.core.logger.Level
import org.koin.dsl.koinApplication

/**
 * Main Application
 */
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        koinApplication {
            useAndroidLogger(Level.DEBUG)
            useAndroidContext(this@MainApplication)
            loadModules(offlineWeatherApp)
        }.start()

        Iconify
            .with(WeathericonsModule())
    }
}
