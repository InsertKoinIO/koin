package koin.sampleapp

import android.app.Application
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.WeathericonsModule
import koin.sampleapp.koin.MyModule
import org.koin.Koin
import org.koin.KoinContext
import org.koin.android.KoinContextAware
import org.koin.android.init

class MainApplication : Application(), KoinContextAware {

    /**
     * Koin context
     */
    lateinit var context: KoinContext

    /**
     * Koin context getter
     */
    override fun getKoin(): KoinContext = context

    override fun onCreate() {
        super.onCreate()

        // init Koin with NetworkModule module
        context = Koin().init(this).build(MyModule())

        Iconify.with(WeathericonsModule())
    }
}
