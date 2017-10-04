package koin.sampleapp

import android.app.Application
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.WeathericonsModule
import koin.sampleapp.di.WebModule
import koin.sampleapp.di.allModules
import org.koin.android.KoinContextAware
import org.koin.android.newKoinContext

class MainApplication : Application(), KoinContextAware {

    // Koin Context
    override val koinContext = newKoinContext(this, allModules())

    override fun onCreate() {
        super.onCreate()

        // Fill url property from Android resource
        koinContext.setProperty(WebModule.SERVER_URL, resources.getString(R.string.server_url))

        Iconify.with(WeathericonsModule())
    }
}
