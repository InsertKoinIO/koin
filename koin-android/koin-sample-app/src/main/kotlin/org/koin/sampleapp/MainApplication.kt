package org.koin.sampleapp

import android.app.Application
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.WeathericonsModule
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.newKoinContext
import org.koin.sampleapp.di.WeatherModule
import org.koin.sampleapp.di.weatherAppModules

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        newKoinContext(this, weatherAppModules())

        // Fill url property from Android resource
        getKoin().setProperty(WeatherModule.SERVER_URL, resources.getString(R.string.server_url))

        Iconify.with(WeathericonsModule())
    }
}
