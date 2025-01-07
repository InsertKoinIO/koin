package org.koin.sample.sandbox

import android.app.Application
import android.os.StrictMode
import androidx.work.WorkManager
import androidx.work.await
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.androix.startup.KoinStartup
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.lazyModules
import org.koin.core.logger.Level
import org.koin.core.waitAllStartJobs
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.koinConfiguration
import org.koin.mp.KoinPlatform
import org.koin.sample.sandbox.di.allModules


class MainApplication : Application(), KoinStartup {

    override fun onKoinStartup() = koinConfiguration {
        androidLogger(Level.DEBUG)
        androidContext(this@MainApplication)
        androidFileProperties()
        fragmentFactory()
        workManagerFactory()
//            lazyModules(allModules, dispatcher = IO)
        modules(allModules)
    }

//    override fun onKoinStartup() : KoinAppDeclaration = {
//        androidLogger(Level.DEBUG)
//        androidContext(this@MainApplication)
//        androidFileProperties()
//        fragmentFactory()
//        workManagerFactory()
//        modules(allModules)
//    }

    companion object {
        var startTime: Long = 0
    }

    override fun onCreate() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectAll() // or .detectAll() for all detectable problems
                .penaltyLog()
                .build()
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build()
        )
        super.onCreate()

        startTime = System.currentTimeMillis()

//        startKoin {
//            androidLogger(Level.DEBUG)
//            androidContext(this@MainApplication)
//            androidFileProperties()
//            fragmentFactory()
//            workManagerFactory()
////            lazyModules(allModules, dispatcher = IO)
//            modules(allModules)
//        }

        //TODO Load/Unload Koin modules scenario cases
        cancelPendingWorkManager(this)

        KoinPlatform.getKoin().waitAllStartJobs()
    }
}

/**
 * If there is a pending work because of previous crash we'd like it to not run.
 *
 */
private fun cancelPendingWorkManager(mainApplication: MainApplication) {
    runBlocking {
        WorkManager.getInstance(mainApplication)
                .cancelAllWork()
                .result
                .get()
    }
}
