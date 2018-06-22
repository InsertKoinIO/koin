package fr.ekito.myweatherapp

import android.app.Application
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.WeathericonsModule
import fr.ekito.myweatherapp.di.offlineWeatherApp
import org.koin.android.ext.android.startKoin

/**
 * Main Application
 */
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // start Koin context
        startKoin(this, offlineWeatherApp)

        Iconify
            .with(WeathericonsModule())
    }
}
