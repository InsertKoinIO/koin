package org.koin.sampleapp

import android.app.Application
import android.content.pm.ApplicationInfo
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.WeathericonsModule
import org.koin.Koin
import org.koin.android.ext.android.bindString
import org.koin.android.ext.android.startAndroidContext
import org.koin.android.logger.AndroidLogger
import org.koin.sampleapp.di.WeatherModule
import org.koin.sampleapp.di.weatherAppModules

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startAndroidContext(this, weatherAppModules())
        val isDebug = (0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE)
        if (isDebug) {
            Koin.logger = AndroidLogger()
        }

        // bind R.string.server_url to Koin WeatherModule.SERVER_URL
        bindString(R.string.server_url, WeatherModule.SERVER_URL)

        Iconify.with(WeathericonsModule())
    }
}
