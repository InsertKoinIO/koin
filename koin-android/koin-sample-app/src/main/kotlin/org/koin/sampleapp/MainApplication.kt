package org.koin.sampleapp

import android.app.Application
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.WeathericonsModule
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.bindString
import org.koin.android.ext.koin.startAndroidContext
import org.koin.sampleapp.di.WeatherModule
import org.koin.sampleapp.di.weatherAppModules

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startAndroidContext(this, weatherAppModules())

        // bind R.string.server_url to Koin WeatherModule.SERVER_URL
        getKoin().bindString(R.string.server_url, WeatherModule.SERVER_URL)

        Iconify.with(WeathericonsModule())
    }
}
