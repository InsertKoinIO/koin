package org.koin.sampleapp

import android.app.Application
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.WeathericonsModule
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.bindString
import org.koin.android.ext.koin.newContext
import org.koin.sampleapp.di.WeatherModule
import org.koin.sampleapp.di.weatherAppModules

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        newContext(this, weatherAppModules())

        // Fill url property from Android resource
        getKoin().bindString(R.string.server_url, WeatherModule.SERVER_URL)

        Iconify.with(WeathericonsModule())
    }
}
