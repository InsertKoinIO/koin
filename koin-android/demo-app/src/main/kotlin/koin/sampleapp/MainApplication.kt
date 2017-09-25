package koin.sampleapp

import android.app.Application
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.WeathericonsModule
import koin.sampleapp.di.WebModule
import koin.sampleapp.di.allModules
import org.koin.Koin
import org.koin.KoinContext
import org.koin.android.KoinContextAware
import org.koin.android.init

class MainApplication : Application(), KoinContextAware {

    /**
     * KoinContext property
     */
    lateinit var koinContext: KoinContext

    /**
     * KoinContext getter
     */
    override fun getKoin(): KoinContext = koinContext

    override fun onCreate() {
        super.onCreate()

        // init Koin with NetworkModule module
        koinContext = Koin().init(this).build(allModules())
        // Fill url property
        koinContext.setProperty(WebModule.SERVER_URL, resources.getString(R.string.server_url))

        Iconify.with(WeathericonsModule())
    }
}
